package com.golajugaenyang.auth.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtKeyProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final RSAKey publicRsaKey;

    public JwtKeyProvider(@Value("${jwt.rsa.private-key}") String privateKeyPem) {
        try {
            this.privateKey = parsePrivateKey(privateKeyPem);
            this.publicKey = derivePublicKey((RSAPrivateCrtKey) this.privateKey);

            this.publicRsaKey = new RSAKey.Builder((RSAPublicKey) publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyIDFromThumbprint()
                .build();
        } catch (GeneralSecurityException | JOSEException e) {
            throw new IllegalStateException("RSA 키를 초기화할 수 없습니다.", e);
        }
    }

    private static PrivateKey parsePrivateKey(String pem) throws GeneralSecurityException {
        String base64Body = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(base64Body);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private static PublicKey derivePublicKey(RSAPrivateCrtKey privateCrtKey) throws GeneralSecurityException {
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
            privateCrtKey.getModulus(),
            privateCrtKey.getPublicExponent()
        );
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }

    public String getKeyId() {
        return publicRsaKey.getKeyID();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public JWKSet getPublicJWKSet() {
        return new JWKSet(publicRsaKey);
    }
}
