package com.ip.apigateway.security;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

    public CustomAuthenticationManagerResolver(CustomJwtAuthenticationConverter customJwtAuthenticationConverter,
                                               GoogleJwtAuthenticationConverter googleJwtAuthenticationConverter,
                                               GuestJwtAuthenticationConverter guestJwtAuthenticationConverter,
                                               String googleIssuer,
                                               String googleJwkUri,
                                               String imageProcessingIssuer,
                                               String imageProcessingJwkUri,
                                               String guestIssuer,
                                               String guestJwtUri
    ) {
        JwtDecoder imageDecoder = NimbusJwtDecoder.withJwkSetUri(imageProcessingJwkUri).build();
        JwtAuthenticationProvider imageProvider = new JwtAuthenticationProvider(imageDecoder);
        imageProvider.setJwtAuthenticationConverter(customJwtAuthenticationConverter);
        authenticationManagers.put(imageProcessingIssuer, imageProvider::authenticate);

        JwtDecoder googleDecoder = NimbusJwtDecoder.withJwkSetUri(googleJwkUri).build();
        JwtAuthenticationProvider googleProvider = new JwtAuthenticationProvider(googleDecoder);
        googleProvider.setJwtAuthenticationConverter(googleJwtAuthenticationConverter);
        authenticationManagers.put(googleIssuer, googleProvider::authenticate);

        JwtDecoder guestDecoder = NimbusJwtDecoder.withJwkSetUri(guestJwtUri).build();
        JwtAuthenticationProvider guestProvider = new JwtAuthenticationProvider(guestDecoder);
        guestProvider.setJwtAuthenticationConverter(guestJwtAuthenticationConverter);
        authenticationManagers.put(guestIssuer, guestProvider::authenticate);
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {
        String header = context.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String token = header.substring(7);
        String issuer;
        try {
            SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
            issuer = signedJWT.getJWTClaimsSet().getIssuer();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT", e);
        }
        AuthenticationManager manager = authenticationManagers.get(issuer);
        if (manager == null) {
            throw new IllegalArgumentException("Unknown issuer: " + issuer);
        }

        return manager;
    }
}
