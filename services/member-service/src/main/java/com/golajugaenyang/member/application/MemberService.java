package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.in.web.dto.SignupRequest;
import com.golajugaenyang.member.domain.entity.Agreement;
import com.golajugaenyang.member.domain.entity.Member;
import com.golajugaenyang.member.domain.entity.enums.AgreementType;
import com.golajugaenyang.member.error.MemberErrorCode;
import com.golajugaenyang.member.domain.repository.AgreementRepository;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    // 약관 버전/만료 정책이 아직 PM으로부터 확정되지 않아 임시로 고정값을 사용한다.
    private static final String CURRENT_AGREEMENT_VERSION = "1.0";
    private static final LocalDateTime AGREEMENT_NO_EXPIRY = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private final MemberRepository memberRepo;
    private final AgreementRepository agreementRepo;
    private final NicknameGenerator nicknameGenerator;

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = nicknameGenerator.generate();
        } while (memberRepo.existsByNickname(nickname));
        return nickname;
    }

    @Transactional
    public void signUp(Long authId, String nickname, List<SignupRequest.AgreementItem> agreements) {
        validateNoDuplicateAgreementTypes(agreements);
        validateRequiredAgreementsAgreed(agreements);
        alreadySignedAuthId(authId);
        checkUniqueNickname(nickname);

        Member savedMember = memberRepo.save(
            new Member(null, nickname, null, null, null, null, null, null, authId)
        );

        LocalDateTime now = LocalDateTime.now();
        List<Agreement> agreementsToSave = agreements.stream()
            .map(item -> new Agreement(
                null, item.type(), CURRENT_AGREEMENT_VERSION, item.agreed(),
                now, AGREEMENT_NO_EXPIRY, savedMember.getId()
            ))
            .toList();

        agreementRepo.saveAll(agreementsToSave);
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

    private void alreadySignedAuthId(Long authId){
        if(memberRepo.existsByAuthId(authId)){
            throw new AppException(MemberErrorCode.ALREADY_SIGNED_UP);
        }
    }

    private void checkUniqueNickname(String nickname){
        if(memberRepo.existsByNickname(nickname)){
            throw new AppException(MemberErrorCode.ALREADY_HAVE_NICKNAME);
        }
    }
}
