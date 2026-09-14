package com.golajugaenyang.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_ID_HEADER = "X-Auth-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authId = request.getHeader(AUTH_ID_HEADER);
        if (StringUtils.hasText(authId)) {
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authId, null, List.of())
            );
        }

        filterChain.doFilter(request, response);
    }
}
