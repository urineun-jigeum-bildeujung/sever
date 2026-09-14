package com.golajugaenyang.member.domain.repository;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.domain.entity.BreedMaster;
import java.util.List;

public interface BreedMasterRepository {

    List<BreedMaster> findBySpecies(Species species);

}
