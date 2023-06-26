package com.example.demo.user;

import com.example.demo.AbstractTestContainers;
import com.example.demo.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;


@RunWith(MockitoJUnitRunner.class)
class UserRepositoryTest extends AbstractTestContainers {
    @InjectMocks
    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void searchUsers() {
// Given
        String login = "Wiktor";
        Date lastLoginDateFrom = new Date();
        Date lastLoginDateTo = new Date();
        int shapesCreatedFrom = 5;
        int shapesCreatedTo = 10;
        List<User> expectedUsers = Arrays.asList(
                User.builder()
                        .login("Wiktor")
                        .lastLoginDate(new Date())
                        .shapes(Collections.emptyList())
                        .build(),
                User.builder()
                        .login("Piotr")
                        .lastLoginDate(new Date())
                        .shapes(Collections.emptyList())
                        .build()
        );
    }

    @Test
    void findUserByLogin() {
        // Given
        String login = "Wiktor";
        Optional<User> expectedUserOptional = Optional.of(
                User.builder()
                        .login("Wiktor")
                        .lastLoginDate(new Date())
                        .shapes(Collections.emptyList())
                        .build()
        );
        User expectedUser = expectedUserOptional.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Mockito.when(userRepository.findUserByLogin(Mockito.eq(login))).thenReturn(expectedUserOptional);

        // When
        User user = userRepository.findUserByLogin(login).orElseThrow(()->new ResourceNotFoundException("User not found"));

        // Then
        assertThat(expectedUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(expectedUser.getLastLoginDate()).isEqualTo(user.getLastLoginDate());
        Assertions.assertThat(expectedUser.getShapes()).isEqualTo(user.getShapes());

    }
}
