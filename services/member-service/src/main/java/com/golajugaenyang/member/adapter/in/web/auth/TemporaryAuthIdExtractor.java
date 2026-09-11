package com.golajugaenyang.member.adapter.in.web.auth;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.error.MemberErrorCode;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;

/**
 * 게이트웨이 JWT 검증(X-Auth-Id 헤더 전달) 방식이 확정되기 전까지의 임시 조치.
 * 서명 검증 없이 access 토큰의 sub(authId)만 읽는다 — 확정되면 이 클래스는 삭제한다.
 */
public final class TemporaryAuthIdExtractor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private TemporaryAuthIdExtractor() {
    }

    public static Long extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AppException(MemberErrorCode.UNAUTHENTICATED);
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            if (!ACCESS_TOKEN_TYPE.equals(claims.getStringClaim(TOKEN_TYPE_CLAIM))) {
                throw new AppException(MemberErrorCode.UNAUTHENTICATED);
            }
            return Long.parseLong(claims.getSubject());
        } catch (ParseException | NumberFormatException e) {
            throw new AppException(MemberErrorCode.UNAUTHENTICATED);
        }
    }
}
