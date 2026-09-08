package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.common.core.domain.Species;
import com.golajugaenyang.member.domain.entity.enums.Sex;
import com.golajugaenyang.member.domain.entity.enums.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Pet {

    private Long id;
    private boolean isDefault;
    private String name;
    private Sex sex;
    private boolean isNeutered;
    private Species species;
    private int age;
    private LocalDate birthDate;
    private Size size;
    private String weight;
    private int bcs;
    private String image;
    private LocalDateTime deletedAt;
    private Long memberId;
    private Long breedId;

    public Pet(Long id, boolean isDefault, String name, Sex sex,
        boolean isNeutered, Species species, int age, LocalDate birthDate,
        Size size, String weight, int bcs, String image, LocalDateTime deletedAt,
        Long memberId, Long breedId){
        this.id = id;
        this.isDefault = isDefault;
        this.name = name;
        this.sex = sex;
        this.isNeutered = isNeutered;
        this.species = species;
        this.age = age;
        this.birthDate = birthDate;
        this.size = size;
        this.weight = weight;
        this.bcs = bcs;
        this.image = image;
        this.deletedAt = deletedAt;
        this.memberId = memberId;
        this.breedId = breedId;
    }
}
