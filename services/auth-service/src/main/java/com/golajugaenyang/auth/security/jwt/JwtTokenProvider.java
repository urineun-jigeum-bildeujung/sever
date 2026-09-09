package com.golajugaenyang.auth.security.jwt;

import com.nimbusds.jose.jwk.RSAKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final RSAKey publicRasKey;

    public JwtTokenProvider(@Value("${jwt.rsa.private-key}") String privateKeyPem){

    }

}
