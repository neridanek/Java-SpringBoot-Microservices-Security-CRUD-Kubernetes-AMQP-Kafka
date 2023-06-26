package com.example.demo.user;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getUsers() throws Exception {
        // Given
        String login = "Wiktor";
        Date lastLoginDateFrom = new Date();
        Date lastLoginDateTo = new Date();
        int shapesCreatedFrom = 5;
        int shapesCreatedTo = 10;
        User user1 = User.builder().login("Wiktor")
                .lastLoginDate(new Date()).shapes(List.of())
                .password("asd").role(User.Role.valueOf("CREATOR"))
                .build();

        User user2 = User.builder().login("Piotr")
                .lastLoginDate(new Date()).shapes(List.of())
                .password("fds").role(User.Role.valueOf("CREATOR"))
                .build();

        List<User> expectedUsers = Arrays.asList(user1, user2);

        // When
        when(userService.searchUsersByParameters(
                login,
                lastLoginDateFrom,
                lastLoginDateTo,
                shapesCreatedFrom,
                shapesCreatedTo))
                .thenReturn(expectedUsers);

        // Then
        mockMvc.perform(get("/api/v1/users")
                        .param("login", login)
                        .param("lastLoginDateFrom", lastLoginDateFrom.toString())
                        .param("lastLoginDateTo", lastLoginDateTo.toString())
                        .param("shapesCreatedFrom", String.valueOf(shapesCreatedFrom))
                        .param("shapesCreatedTo", String.valueOf(shapesCreatedTo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value(user1.getLogin()))
                .andExpect(jsonPath("$[1].login").value(user2.getLogin()));
    }
}
