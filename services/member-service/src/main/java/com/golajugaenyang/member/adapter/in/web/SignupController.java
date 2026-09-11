package com.golajugaenyang.member.adapter.in.web;

import com.golajugaenyang.member.adapter.in.web.auth.TemporaryAuthIdExtractor;
import com.golajugaenyang.member.adapter.in.web.dto.SignupRequest;
import com.golajugaenyang.member.application.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SignupController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @Valid @RequestBody SignupRequest request
    ) {
        Long authId = TemporaryAuthIdExtractor.extract(authorization);
        memberService.signUp(authId, request.nickname(), request.agreements());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
