package com.example.demo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByParameters(
            UserRequest userRequest) {
        return userRepository.searchUsers(
                userRequest.getLogin(),
                userRequest.getLastLoginDateFrom(),
                userRequest.getLastLoginDateTo(),
                userRequest.getShapesCreatedFrom(),
                userRequest.getShapesCreatedTo());
    }
}

