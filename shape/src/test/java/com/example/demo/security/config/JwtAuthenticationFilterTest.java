package com.example.demo.security.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void authenticateUserWithValidJwtToken() throws ServletException, IOException {
        String token = "valid_token";
        String username = "wiktorn";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getServletPath()).thenReturn("/api/v1/resource");


        when(jwtService.extractUsername(eq(token))).thenReturn(username);
        when(jwtService.isTokenValid(eq(token), any(UserDetails.class))).thenReturn(true);


        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(eq(username))).thenReturn(userDetails);


        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic =
                     Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            filter.doFilterInternal(request, response, filterChain);

            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));

            verify(filterChain).doFilter(request, response);
        }
    }
}
