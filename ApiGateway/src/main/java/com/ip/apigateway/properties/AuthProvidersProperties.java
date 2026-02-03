package com.ip.apigateway.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2")
@Getter
@Setter
public class AuthProvidersProperties {

    private Provider google;
    private Provider ip;
    private Provider guest;

    @Getter
    @Setter
    public static class Provider {
        private String issuer;
        private String jwkUri;
    }
}
