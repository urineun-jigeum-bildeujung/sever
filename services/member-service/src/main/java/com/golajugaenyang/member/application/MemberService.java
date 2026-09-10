package com.golajugaenyang.member.application;

import com.golajugaenyang.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepo;
    private final NicknameGenerator nicknameGenerator;

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = nicknameGenerator.generate();
        } while (memberRepo.existsByNickname(nickname));
        return nickname;
    }
}
