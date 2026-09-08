package com.golajugaenyang.member.domain.entity;

import com.golajugaenyang.member.domain.entity.enums.AgreementType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Agreement {

    private Long id;
    private AgreementType agreementType;
    private String agreementVersion;
    private boolean isAgreed;
    private LocalDateTime agreedAt;
    private LocalDateTime expiredAt;
    private Long memberId;

    public Agreement(Long id, AgreementType agreementType, String agreementVersion,
        boolean isAgreed,
        LocalDateTime agreedAt, LocalDateTime expiredAt, Long memberId) {
        this.id = id;
        this.agreementType = agreementType;
        this.agreementVersion = agreementVersion;
        this.isAgreed = isAgreed;
        this.agreedAt = agreedAt;
        this.expiredAt = expiredAt;
        this.memberId = memberId;
    }


}
