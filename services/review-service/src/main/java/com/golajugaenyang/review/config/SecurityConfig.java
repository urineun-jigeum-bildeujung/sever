package com.golajugaenyang.review.config;

import com.golajugaenyang.common.security.filter.HeaderAuthenticationEntryPoint;
import com.golajugaenyang.common.security.filter.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new HeaderAuthenticationEntryPoint()))
            .authorizeHttpRequests(auth -> auth
                // 순서 중요: /me, 상태체크 피드백 제출이 더 뒤의 permitAll 패턴에도
                // 매칭되므로, 로그인이 필요한 경로를 먼저 명시해서 우선순위를 갖도록 함
                .requestMatchers("/internal/**", "/actuator/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/products/*/feedbacks").authenticated()
                .requestMatchers("/api/v1/reviews/products/**").permitAll()
                .requestMatchers("/api/v1/reviews/me", "/api/v1/reviews/feedbacks/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/*").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
