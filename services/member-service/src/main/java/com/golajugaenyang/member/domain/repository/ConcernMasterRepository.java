package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.domain.entity.ConcernMaster;
import java.util.List;

public interface ConcernMasterRepository {

    List<ConcernMaster> findByConcernCodeIn(List<String> concernCodes);

    List<ConcernMaster> findBySpecies(Species species);

}
