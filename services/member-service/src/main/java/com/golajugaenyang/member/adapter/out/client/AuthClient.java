package com.golajugaenyang.member.adapter.out.client;

import com.golajugaenyang.common.security.config.InternalFeignClientConfig;
import com.golajugaenyang.member.adapter.out.client.dto.PhoneConfirmRequest;
import com.golajugaenyang.member.adapter.out.client.dto.PhoneConfirmResponse;
import com.golajugaenyang.member.adapter.out.client.dto.TokenPairResponse;
import com.golajugaenyang.member.adapter.out.client.dto.TokenReissueRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth-service.url}", configuration = InternalFeignClientConfig.class)
public interface AuthClient {

    @PostMapping("/internal/v1/auths/token/reissue")
    TokenPairResponse reissueToken(@RequestBody TokenReissueRequest request);

    @PostMapping("/internal/v1/auths/phone/verify-confirm")
    PhoneConfirmResponse verifyPhoneCode(@RequestBody PhoneConfirmRequest request);
}
