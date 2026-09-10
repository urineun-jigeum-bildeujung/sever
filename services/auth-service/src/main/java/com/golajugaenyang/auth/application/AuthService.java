package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.application.recods.AuthLoginResult;
import com.golajugaenyang.auth.application.recods.LoginCodePayload;
import com.golajugaenyang.auth.application.recods.TokenPair;
import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.auth.domain.exception.AuthErrorCode;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.auth.security.jwt.JwtIssuer;
import com.golajugaenyang.auth.security.jwt.LoginCodeStore;
import com.golajugaenyang.auth.security.jwt.RefreshTokenStore;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtIssuer jwtIssuer;
    private final RefreshTokenStore refreshTokenStore;
    private final LoginCodeStore loginCodeStore;

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
        String accessToken = jwtIssuer.generateAccessToken(authId);
        String refreshToken = jwtIssuer.generateRefreshToken(authId);
        refreshTokenStore.save(authId, refreshToken, jwtIssuer.getRefreshTokenExpirationSeconds());
        return new TokenPair(accessToken, refreshToken);
    }

    public String issueLoginCode(Long authId, boolean isNewUser) {
        TokenPair tokenPair = issueTokens(authId);
        // TODO: MemberClient 구현 후 신규 유저일 때 실제 랜덤 닉네임으로 교체
        String nickname = null;
        LoginCodePayload payload = new LoginCodePayload(
            tokenPair.accessToken(), tokenPair.refreshToken(), isNewUser, nickname
        );

        String code = UUID.randomUUID().toString();
        loginCodeStore.save(code, payload);
        return code;
    }

    public LoginCodePayload exchangeLoginCode(String code) {
        return loginCodeStore.consume(code)
            .orElseThrow(() -> new AppException(AuthErrorCode.INVALID_LOGIN_CODE));
    }
}
