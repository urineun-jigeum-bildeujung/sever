package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.member.adapter.in.web.dto.NicknameResponse;
import com.golajugaenyang.member.application.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class NicknameController {

    private final NicknameGenerator nicknameGenerator;

    @GetMapping("/nickname")
    public NicknameResponse getNickname(){
        String nickname = nicknameGenerator.generate();
        return new NicknameResponse(nickname);
    }

}
