package com.ip.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class GoogleJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    /**
     * Switching all claims in Google JWT to roles.
     * @param source a jwt
     * @return a jwt token
     */
    @Override
    public JwtAuthenticationToken convert(Jwt source) {
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
        return new JwtAuthenticationToken(source, authorities);
    }
}
