package com.golajugaenyang.auth.security;

import com.golajugaenyang.common.security.filter.HeaderAuthenticationEntryPoint;
import com.golajugaenyang.common.security.filter.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CustomOAuth2UserService customOAuth2UserService,
        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
        OAuth2LoginFailureHandler oAuth2LoginFailureHandler
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new HeaderAuthenticationEntryPoint()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/oauth2/authorization/**", "/api/auth/login/oauth2/code/**",
                    "/api/v1/auths/token/exchange", "/oauth2/jwks", "/internal/**", "/actuator/**")
                .permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .authorizationEndpoint(auth -> auth.baseUri("/api/auth/oauth2/authorization"))
                .redirectionEndpoint(redirect -> redirect.baseUri("/api/auth/login/oauth2/code/*"))
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
            );
        return http.build();
    }

}