package com.example.demo.user;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllUsers() {
        //Given
        User user1 = User.builder().login("Wiktor")
                .lastLoginDate(new Date())
                .password("asd").role(User.Role.valueOf("CREATOR"))
                .build();


        User user2 = User.builder().login("Piotr")
                .lastLoginDate(new Date())
                .password("fds").role(User.Role.valueOf("CREATOR"))
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> expectedUsers = List.of(user1, user2);

        //When
        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> actualUsers = underTest.getAllUsers();

        //Then
        verify(userRepository).findAll();
        assertEquals(expectedUsers,actualUsers);
    }

    @Test
    void searchUsersByParameters() {
        //Given
        String login = "piotr";
        Date lastLoginDateFrom = new Date();
        Date lastLoginDateTo = new Date();
        int shapesCreatedFrom = 0;
        int shapesCreatedTo = 2;
        User user1 = User.builder().login("Wiktor")
                .lastLoginDate(new Date()).shapes(List.of())
                .password("asd").role(User.Role.valueOf("CREATOR"))
                .build();


        User user2 = User.builder().login("Piotr")
                .lastLoginDate(new Date()).shapes(List.of())
                .password("fds").role(User.Role.valueOf("CREATOR"))
                .build();

        List<User> expectedUsers = List.of(user1, user2);

        //When
        when(userRepository.searchUsers(login,lastLoginDateFrom
                ,lastLoginDateTo,shapesCreatedFrom,shapesCreatedTo))
                .thenReturn(expectedUsers);

        List<User> actualUsers = underTest.searchUsersByParameters(
                login,
                lastLoginDateFrom,
                lastLoginDateTo,
                shapesCreatedFrom,
                shapesCreatedTo);

        //Then
        verify(userRepository).searchUsers(login,lastLoginDateFrom,
                lastLoginDateTo,shapesCreatedFrom,
                shapesCreatedTo);

        assertEquals(expectedUsers,actualUsers);

    }
}