package com.example.demo.security.auth;
import com.example.clients.fraud.FraudClient;
import com.example.demo.security.config.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.example.amqp.RabbitMQMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private FraudClient fraudClient;
    @Mock
    private RabbitMQMessageProducer rabbitMQMessageProducer;
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        authenticationService = new AuthenticationService(userRepository, passwordEncoder, jwtService, authenticationManager, fraudClient, rabbitMQMessageProducer);
    }

    @Test
    public void testAuthenticate_ShouldAuthenticateUserAndReturnAuthenticationResponseWithJwt() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("wiktorn", "password");
        User user = User.builder().login(authenticationRequest.getLogin()).lastLoginDate(new Date())
                .password("encodedPassword").role(User.Role.CREATOR).build();
        String jwt = "generatedJwt";

        when(userRepository.findUserByLogin(authenticationRequest.getLogin())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwt);

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(jwt, response.getJwt());
        verify(userRepository).findUserByLogin(authenticationRequest.getLogin());
        verify(jwtService).generateToken(user);
    }



}