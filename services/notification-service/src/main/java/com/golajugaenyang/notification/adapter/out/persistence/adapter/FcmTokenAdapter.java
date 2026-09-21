package com.golajugaenyang.notification.adapter.out.persistence.adapter;

import com.golajugaenyang.notification.adapter.out.persistence.entity.FcmTokenJpaEntity;
import com.golajugaenyang.notification.adapter.out.persistence.repository.FcmTokenJpaRepository;
import com.golajugaenyang.notification.domain.repository.FcmTokenRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FcmTokenAdapter implements FcmTokenRepository {

    private final FcmTokenJpaRepository fcmTokenJpaRepo;

    @Override
    @Transactional
    public void upsert(Long memberId, String token) {
        fcmTokenJpaRepo.findByToken(token)
                .ifPresentOrElse(
                        existing -> existing.setMemberId(memberId),
                        () -> fcmTokenJpaRepo.save(
                                FcmTokenJpaEntity.builder().memberId(memberId).token(token).build())
                );
    }

    @Override
    public Map<Long, List<String>> findTokensByMemberIds(List<Long> memberIds) {
        return fcmTokenJpaRepo.findByMemberIdIn(memberIds).stream()
                .collect(Collectors.groupingBy(
                        FcmTokenJpaEntity::getMemberId,
                        Collectors.mapping(FcmTokenJpaEntity::getToken, Collectors.toList())
                ));
    }

    @Override
    public List<Long> findAllMemberIds() {
        return fcmTokenJpaRepo.findDistinctMemberIds();
    }
}
