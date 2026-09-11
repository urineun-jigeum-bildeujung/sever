package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.member.adapter.in.web.dto.NicknameResponse;
import com.golajugaenyang.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/members")
@RequiredArgsConstructor
public class NicknameController {

    private final MemberService memberService;

    @GetMapping("/nickname")
    public NicknameResponse getNickname(){
        String nickname = memberService.generateUniqueNickname();
        return new NicknameResponse(nickname);
    }

}
