package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.member.adapter.in.web.dto.MemberIdResponse;
import com.golajugaenyang.member.adapter.in.web.dto.NicknameResponse;
import com.golajugaenyang.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/members")
@RequiredArgsConstructor
public class InternalMemberController {

    private final MemberService memberService;

    @GetMapping("/nickname")
    public NicknameResponse getNickname(){
        String nickname = memberService.generateUniqueNickname();
        return new NicknameResponse(nickname);
    }

    @GetMapping("/me")
    public MemberIdResponse getMemberId(@RequestParam Long authId){
        Long memberId = memberService.getMemberIdByAuthId(authId);
        return new MemberIdResponse(memberId);
    }

}
