package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import com.example.demo.jwt.JwtAuthenticationFilter;
import com.example.demo.services.oauth2.security.CustomOAuth2UserDetailService;
import com.example.demo.services.oauth2.security.handler.CustomOAuth2FailureHandler;
import com.example.demo.services.oauth2.security.handler.CustomOAuth2SuccessHandler;

@ExtendWith(MockitoExtension.class)
public class WebSecurityConfigTest {

    @Mock
    private CustomOAuth2UserDetailService customOAuth2UserDetailService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;

    @Mock
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @BeforeEach
    void setUp() {
    }

    /* S008 */
    @Test
    void jwtAuthenticationFilter_ShouldReturnNewFilter() {
        // Act
        JwtAuthenticationFilter filter = webSecurityConfig.jwtAuthenticationFilter();

        // Assert
        assertNotNull(filter);
    }

    /* S009 */
    @Test
    void authenticationManager_ShouldReturnManagerFromConfiguration() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = webSecurityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertSame(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

}