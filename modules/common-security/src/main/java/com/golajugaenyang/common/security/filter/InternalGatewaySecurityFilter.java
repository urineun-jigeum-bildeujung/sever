package com.golajugaenyang.common.security.filter;

import com.golajugaenyang.common.security.error.CommonSecurityErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriUtils;


@Slf4j
public class InternalGatewaySecurityFilter extends OncePerRequestFilter {

    public static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String AUTH_ID_HEADER = "X-Auth-Id";
    private static final String INTERNAL_PATH_PREFIX = "/internal/";

    private final String expectedSecret;

    public InternalGatewaySecurityFilter(String expectedSecret) {
        if (!StringUtils.hasText(expectedSecret)) {
            throw new IllegalStateException("internal.gateway-secret 값이 설정되지 않았습니다.");
        }
        this.expectedSecret = expectedSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (requiresSecret(request) && !isValidSecret(request.getHeader(INTERNAL_SECRET_HEADER))) {
            log.warn("[InternalGatewaySecurityFilter] 시크릿 검증 실패, uri={}", request.getRequestURI());
            respondUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresSecret(HttpServletRequest request) {
        return claimsIdentity(request) || isInternalPath(request);
    }

    private boolean claimsIdentity(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader(AUTH_ID_HEADER));
    }

    private boolean isInternalPath(HttpServletRequest request) {
        try {
            String decodedPath = UriUtils.decode(request.getRequestURI(), StandardCharsets.UTF_8);
            return decodedPath.startsWith(INTERNAL_PATH_PREFIX);
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private boolean isValidSecret(String providedSecret) {
        if (!StringUtils.hasText(providedSecret)) {
            return false;
        }

        byte[] provided = providedSecret.getBytes(StandardCharsets.UTF_8);
        byte[] expected = expectedSecret.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(provided, expected);
    }

    private void respondUnauthorized(HttpServletResponse response) throws IOException {
        CommonSecurityErrorCode errorCode = CommonSecurityErrorCode.INVALID_INTERNAL_SECRET;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(String.format(
            "{\"errorCode\":\"%s\",\"detail\":\"%s\"}",
            errorCode.getCode(), errorCode.getMessage()
        ));
    }
}
