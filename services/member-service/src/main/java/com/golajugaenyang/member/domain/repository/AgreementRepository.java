package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.member.domain.entity.Agreement;
import java.util.List;

public interface AgreementRepository {
    List<Agreement> saveAll(List<Agreement> agreements);
}
