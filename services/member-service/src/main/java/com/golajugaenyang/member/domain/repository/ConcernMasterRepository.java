package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import java.util.List;

public interface ConcernMasterRepository {

    List<ConcernMaster> findByConcernCodeInAndSpecies(List<String> concernCodes, Species species);

    List<ConcernMaster> findBySpecies(Species species);

    List<ConcernMaster> findByIdIn(List<Long> ids);

}
