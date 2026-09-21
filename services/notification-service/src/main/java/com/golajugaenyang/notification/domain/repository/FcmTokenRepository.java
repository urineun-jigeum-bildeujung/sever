package com.golajugaenyang.notification.domain.repository;

import java.util.List;
import java.util.Map;

public interface FcmTokenRepository {

    void upsert(Long memberId, String token);

    Map<Long, List<String>> findTokensByMemberIds(List<Long> memberIds);

    List<Long> findAllMemberIds();

    void deleteByToken(String token);
}
