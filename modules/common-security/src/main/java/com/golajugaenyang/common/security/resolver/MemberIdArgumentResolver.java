package com.golajugaenyang.common.security.resolver;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.common.security.annotation.MemberId;
import com.golajugaenyang.common.security.error.CommonSecurityErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    public boolean supportsParameter(MethodParameter parameter){
        return parameter.hasParameterAnnotation(MemberId.class)
                && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory){
        String memberIdHeader = webRequest.getHeader(MEMBER_ID_HEADER);

        try{
            return Long.parseLong(memberIdHeader);
        } catch (NumberFormatException e) {
            throw new AppException(CommonSecurityErrorCode.MISSING_MEMBER_ID);
        }
    }

}
