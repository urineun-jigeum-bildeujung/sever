package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.adapter.out.client.MemberClient;
import com.golajugaenyang.auth.application.recods.AuthLoginResult;
import com.golajugaenyang.auth.application.recods.LoginCodePayload;
import com.golajugaenyang.auth.application.recods.TokenPair;
import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.auth.domain.error.AuthErrorCode;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import com.golajugaenyang.auth.security.jwt.*;
import com.golajugaenyang.common.core.exception.AppException;
import com.nimbusds.jwt.JWTClaimsSet;
import feign.FeignException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtIssuer jwtIssuer;
    private final RefreshTokenStore refreshTokenStore;
    private final LoginCodeStore loginCodeStore;
    private final MemberClient memberClient;
    private final JwtVerifier jwtVerifier;
    private final TokenBlacklistPublisher blacklistPublisher;

    public AuthLoginResult findOrCreateAuth(String provider, String socialId, String socialEmail) {
        Optional<Auth> existingAuth = authRepository.findByProviderAndSocialId(provider, socialId);
        if (existingAuth.isPresent()) {
            return new AuthLoginResult(existingAuth.get(), false);
        }

        try {
            Auth auth = authRepository.save(
                new Auth(null, provider, socialId, socialEmail, AuthStatus.ACTIVE, null, null)
            );
            return new AuthLoginResult(auth, true);
        } catch (DataIntegrityViolationException e) {
            Auth auth = authRepository.findByProviderAndSocialId(provider, socialId)
                .orElseThrow(() -> e);
            return new AuthLoginResult(auth, false);
        }
    }

    public TokenPair issueTokens(Long authId) {
        Long memberId = getMemberIdOrNull(authId);
        String accessToken = jwtIssuer.generateAccessToken(authId, memberId);
        String refreshToken = jwtIssuer.generateRefreshToken(authId, memberId);
        refreshTokenStore.save(authId, refreshToken, jwtIssuer.getRefreshTokenExpirationSeconds());
        return new TokenPair(accessToken, refreshToken, memberId);
    }

    public String issueLoginCode(Long authId) {
        TokenPair tokenPair = issueTokens(authId);
        boolean needsSignup = tokenPair.memberId() == null;

        String nickname = null;
        if(needsSignup){
            try{
                nickname = memberClient.getNicknameSuggestion().nickname();
            } catch (Exception e) {
                log.warn("닉네임 제안 조회 실패, authId={}", authId, e);
                nickname = generateFallbackNickname();
            }
        }

        LoginCodePayload payload = new LoginCodePayload(
            tokenPair.accessToken(), tokenPair.refreshToken(), nickname, needsSignup
        );

        String code = UUID.randomUUID().toString();
        loginCodeStore.save(code, payload);
        return code;
    }

    public TokenPair reissueTokens(Long authId, Long memberId) {
        validateMemberOwnership(authId, memberId);
        String accessToken = jwtIssuer.generateAccessToken(authId, memberId);
        String refreshToken = jwtIssuer.generateRefreshToken(authId, memberId);
        refreshTokenStore.save(authId, refreshToken, jwtIssuer.getRefreshTokenExpirationSeconds());
        return new TokenPair(accessToken, refreshToken, memberId);
    }

    public LoginCodePayload exchangeLoginCode(String code) {
        return loginCodeStore.consume(code)
                .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_LOGIN_CODE));
    }

    public TokenPair refreshTokens(String refreshToken) {
        JWTClaimsSet jwtClaimsSet = jwtVerifier.verifyRefreshToken(refreshToken)
                .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_TOKEN));

        Long authId = Long.valueOf(jwtClaimsSet.getSubject());
        Long memberId = getMemberIdOrNull(authId);

        String newAccessToken = jwtIssuer.generateAccessToken(authId, memberId);
        String newRefreshToken = jwtIssuer.generateRefreshToken(authId, memberId);

        boolean rotated = refreshTokenStore.rotate(
            authId, refreshToken, newRefreshToken, jwtIssuer.getRefreshTokenExpirationSeconds());

        if (!rotated) {
            throw new AppException(AuthErrorCode.INVALID_TOKEN);
        }

        return new TokenPair(newAccessToken, newRefreshToken, memberId);
    }

    public void logout(Long authId, String accessToken) {
        JWTClaimsSet jwtClaimsSet = jwtVerifier.verifyAccessToken(accessToken)
                .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_TOKEN));

        refreshTokenStore.delete(authId);

        long ttlSeconds = (jwtClaimsSet.getExpirationTime().getTime() - System.currentTimeMillis()) / 1000;

        blacklistPublisher.publish(accessToken, ttlSeconds);
    }

    public String getMyEmail(Long authId) {
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_AUTH));
        return auth.getSocialEmail();
    }

    private void validateMemberOwnership(Long authId, Long memberId) {
        Long actualMemberId = memberClient.getMemberId(authId).memberId();
        if (!memberId.equals(actualMemberId)) {
            throw new AppException(AuthErrorCode.MEMBER_ID_MISMATCH);
        }
    }

    private String generateFallbackNickname() {
        return "user" + UUID.randomUUID().toString().substring(0, 8);
    }

    private Long getMemberIdOrNull(Long authId){
        try{
            return memberClient.getMemberId(authId).memberId();
        } catch (FeignException.NotFound e){
            return null;
        }
    }
}
