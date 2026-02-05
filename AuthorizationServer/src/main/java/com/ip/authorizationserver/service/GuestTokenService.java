package com.ip.authorizationserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestTokenService {

    private final JWKSource<SecurityContext> jwkSource;

    @Value("${spring.security.oauth2.authorizationserver.issuer-uri}")
    private String issuer;

    public String generateGuestToken() {
        try {
            List<JWK> keys = jwkSource.get(new JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build()), null);
            RSAKey rsaKey = (RSAKey) keys.get(0);

            Instant now = Instant.now();
            String guestId = "guest-" + UUID.randomUUID().toString();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(guestId)
                    .claim("roles", Collections.singletonList("GUEST"))
                    .claim("name", "Guest User")
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(7200)))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(rsaKey));

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Error for creating token for guests", e);
        }
    }
}