package com.example.demo.security.auth;

import com.example.clients.fraud.FraudCheckResponse;
import com.example.clients.fraud.FraudClient;
import com.example.clients.notification.NotificationRequest;
import com.example.demo.security.config.JwtService;
import com.example.demo.user.UserRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.example.amqp.RabbitMQMessageProducer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;


    public AuthenticationResponse register(RegisterRequest request){
        User user = User.builder().login(request.getLogin()).lastLoginDate(new Date())
                .password(passwordEncoder.encode(request.getPassword())).role(User.Role.CREATOR).build();
        userRepository.saveAndFlush(user);
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(user.getId().intValue());

        if (fraudCheckResponse.isFraudster) {
            throw new IllegalArgumentException("fraudster");
        }

        NotificationRequest notificationRequest = NotificationRequest
                .builder().toCustomerId(user.getId().intValue())
                .toCustomerName(user.getUsername())
                .message(String.format("Hello %s, in the Shape App",user.getUsername())).build();

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().jwt(jwt).build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),request.getPassword()));

        var user = userRepository.findUserByLogin(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        user.setLastLoginDate(new Date());
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().jwt(jwt).build();
    }
}
