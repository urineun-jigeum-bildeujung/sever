package com.golajugaenyang.common.security.filter;

import com.golajugaenyang.common.security.error.CommonSecurityErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class HeaderAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException, ServletException {

        CommonSecurityErrorCode errorCode = CommonSecurityErrorCode.MISSING_AUTH_ID;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(String.format(
            "{\"errorCode\":\"%s\",\"detail\":\"%s\"}",
            errorCode.getCode(), errorCode.getMessage()
        ));
    }
}
