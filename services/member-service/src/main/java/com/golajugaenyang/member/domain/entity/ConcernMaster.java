package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import lombok.Getter;

@Getter
public class ConcernMaster {

    private Long id;
    private Species species;
    private String concernCategory;
    private String concernCode;

    public ConcernMaster(Long id, Species species, String concernCategory, String concernCode) {
        this.id = id;
        this.species = species;
        this.concernCategory = concernCategory;
        this.concernCode = concernCode;
    }

}
