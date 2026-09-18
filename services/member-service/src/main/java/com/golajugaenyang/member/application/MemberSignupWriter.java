package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.in.web.dto.request.SignupRequest;
import com.golajugaenyang.member.domain.entity.Agreement;
import com.golajugaenyang.member.domain.entity.Member;
import com.golajugaenyang.member.domain.entity.enums.AgreementType;
import com.golajugaenyang.member.domain.repository.AgreementRepository;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import com.golajugaenyang.member.error.MemberErrorCode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberSignupWriter {

    private static final String CURRENT_AGREEMENT_VERSION = "1.0";
    private static final LocalDateTime AGREEMENT_NO_EXPIRY = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private final MemberRepository memberRepo;
    private final AgreementRepository agreementRepo;

    @Transactional
    public Member persistSignup(Long authId, String nickname, List<SignupRequest.AgreementItem> agreements) {
        validateNoDuplicateAgreementTypes(agreements);
        validateRequiredAgreementsAgreed(agreements);
        checkUniqueNickname(nickname);

        Member savedMember = memberRepo.findByAuthIdIncludingDeleted(authId)
            .map(existing -> reactivateOrReject(existing, nickname))
            .orElseGet(() -> memberRepo.save(
                new Member(null, nickname, null, null, null, null, null, null, null, null, authId)
            ));

        LocalDateTime now = LocalDateTime.now();
        List<Agreement> agreementsToSave = agreements.stream()
            .map(item -> new Agreement(
                null, item.type(), CURRENT_AGREEMENT_VERSION, item.agreed(),
                now, AGREEMENT_NO_EXPIRY, savedMember.getId()
            ))
            .toList();

        agreementRepo.saveAll(agreementsToSave);

        return savedMember;
    }

    private void validateNoDuplicateAgreementTypes(List<SignupRequest.AgreementItem> agreements) {
        long distinctTypeCount = agreements.stream()
            .map(SignupRequest.AgreementItem::type)
            .distinct()
            .count();

        if (distinctTypeCount != agreements.size()) {
            throw new AppException(MemberErrorCode.DUPLICATE_AGREEMENT_TYPE);
        }
    }

    private void validateRequiredAgreementsAgreed(List<SignupRequest.AgreementItem> agreements) {
        Set<AgreementType> agreedTypes = agreements.stream()
            .filter(SignupRequest.AgreementItem::agreed)
            .map(SignupRequest.AgreementItem::type)
            .collect(Collectors.toSet());

        boolean allRequiredAgreed = Arrays.stream(AgreementType.values())
            .filter(AgreementType::isRequired)
            .allMatch(agreedTypes::contains);

        if (!allRequiredAgreed) {
            throw new AppException(MemberErrorCode.REQUIRED_AGREEMENT_NOT_AGREED);
        }
    }

    private Member reactivateOrReject(Member existing, String nickname) {
        if (existing.getDeletedAt() == null) {
            throw new AppException(MemberErrorCode.ALREADY_SIGNED_UP);
        }
        return memberRepo.save(existing.reactivate(nickname));
    }

    private void checkUniqueNickname(String nickname){
        if(memberRepo.existsByNickname(nickname)){
            throw new AppException(MemberErrorCode.ALREADY_HAVE_NICKNAME);
        }
    }
}
