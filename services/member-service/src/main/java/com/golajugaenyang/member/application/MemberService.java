package com.golajugaenyang.member.application;

import com.golajugaenyang.common.core.exception.AppException;
import com.golajugaenyang.member.adapter.in.web.dto.request.MemberProfileUpdateRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.PhoneRegisterRequest;
import com.golajugaenyang.member.adapter.in.web.dto.request.SignupRequest;
import com.golajugaenyang.member.adapter.in.web.dto.response.MemberMyProfileResponse;
import com.golajugaenyang.member.adapter.out.client.AuthClient;
import com.golajugaenyang.member.adapter.out.client.dto.*;
import com.golajugaenyang.member.domain.entity.Member;
import com.golajugaenyang.member.domain.repository.MemberRepository;
import com.golajugaenyang.member.error.MemberErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepo;
    private final NicknameGenerator nicknameGenerator;
    private final AuthClient authClient;
    private final MemberSignupWriter memberSignupWriter;

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = nicknameGenerator.generate();
        } while (memberRepo.existsByNickname(nickname));
        return nickname;
    }

    public TokenPairResponse signUp(Long authId, String nickname, List<SignupRequest.AgreementItem> agreements) {
        Member savedMember = memberSignupWriter.persistSignup(authId, nickname, agreements);
        return authClient.reissueToken(new TokenReissueRequest(authId, savedMember.getId()));
    }

    public Long getMemberIdByAuthId(Long authId){
        return memberRepo.findByAuthId(authId)
                .orElseThrow(() -> new AppException(MemberErrorCode.NOT_FOUND)).getId();
    }

    public void registerPhone(Long memberId, PhoneRegisterRequest request) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(()-> new AppException(MemberErrorCode.NOT_FOUND));

        PhoneConfirmResponse response = authClient.verifyPhoneCode(new PhoneConfirmRequest(request.phone(), request.code()));

        if (!response.verified()) {
            throw new AppException(MemberErrorCode.INVALID_PHONE_CODE);
        }

        memberRepo.save(member.withPhone(request.phone(), request.carrier()));
    }

    public MemberMyProfileResponse getMyProfile(Long authId, Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(()-> new AppException(MemberErrorCode.NOT_FOUND));

        MemberMyEmailResponse response = authClient.getMyEmail(new MemberMyEmailRequest(authId));

        return new MemberMyProfileResponse(member.getNickname(), member.getName(), member.getBirth(),
                member.getPhone(), member.getProfileImage(), response.email());
    }

    public void updateProfile(Long memberId, MemberProfileUpdateRequest request) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(()-> new AppException(MemberErrorCode.NOT_FOUND));

        memberRepo.save(member.update(request.nickname(), request.name(), request.birth(), request.image()));
    }
}
