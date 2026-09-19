package com.golajugaenyang.auth.domain.repository;

import com.golajugaenyang.auth.domain.entity.Auth;
import java.util.Optional;

public interface AuthRepository {

    Optional<Auth> findByProviderAndSocialId(String provider, String socialId);
    Optional<Auth> findById(Long authId);
    Auth save(Auth auth);
}
