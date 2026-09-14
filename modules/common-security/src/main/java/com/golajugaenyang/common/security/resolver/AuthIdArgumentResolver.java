package com.golajugaenyang.common.security.resolver;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.security.annotation.AuthId;
import com.golajugaenyang.common.security.error.CommonSecurityErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_ID_HEADER = "X-Auth-Id";

    public boolean supportsParameter(MethodParameter parameter){
        return parameter.hasParameterAnnotation(AuthId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory){
        String authIdHeader = webRequest.getHeader(AUTH_ID_HEADER);

        try{
            return Long.parseLong(authIdHeader);
        } catch (NumberFormatException e) {
            throw new AppException(CommonSecurityErrorCode.MISSING_AUTH_ID);
        }
    }
}
