package com.golajugaenyang.auth.adapter.out.persistence.adapter;

import com.golajugaenyang.auth.adapter.out.persistence.entity.AuthJpaEntity;
import com.golajugaenyang.auth.adapter.out.persistence.mapper.AuthMapper;
import com.golajugaenyang.auth.adapter.out.persistence.repository.AuthJpaRepository;
import com.golajugaenyang.auth.domain.entity.Auth;
import com.golajugaenyang.auth.domain.repository.AuthRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthRepository {

    private final AuthJpaRepository authJpaRepo;


    @Override
    public Auth save(Auth auth){
        AuthJpaEntity jpaEntity = AuthMapper.toJpaEntity(auth);
        AuthJpaEntity savedEntity = authJpaRepo.save(jpaEntity);
        return AuthMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Auth> findByProviderAndSocialId(String provider, String socialId) {
        return authJpaRepo.findByProviderAndSocialId(provider, socialId)
            .map(AuthMapper::toDomain);
    }
}
