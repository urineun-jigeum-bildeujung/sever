package com.golajugaenyang.auth.adapter.out.persistence.repository;

import com.golajugaenyang.auth.adapter.out.persistence.entity.AuthJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthJpaRepository extends JpaRepository<AuthJpaEntity, Long> {

    Optional<AuthJpaEntity> findByProviderAndSocialId(String provider, String socialId);
}
