package com.golajugaenyang.auth.application;

import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.entity.enums.AuthStatus;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import com.golajugaenyang.auth.security.jwt.JwtIssuer;
import com.golajugaenyang.auth.security.jwt.RefreshTokenStore;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtIssuer jwtIssuer;
    private final RefreshTokenStore refreshTokenStore;

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
}
