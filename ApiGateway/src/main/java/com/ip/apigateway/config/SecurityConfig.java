package com.ip.apigateway.config;

import com.ip.apigateway.security.CustomAuthenticationManagerResolver;
import com.ip.apigateway.security.CustomJwtAuthenticationConverter;
import com.ip.apigateway.security.GoogleJwtAuthenticationConverter;
import com.ip.apigateway.security.GuestJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.google.issuer}")
    private String googleIssuer;
    @Value("${spring.security.oauth2.google.jwk-uri}")
    private String googleJwkUri;
    @Value("${spring.security.oauth2.ip.issuer}")
    private String imageProcessingIssuer;
    @Value("${spring.security.oauth2.ip.jwk-uri}")
    private String imageProcessingJwkUri;
    @Value("${spring.security.oauth2.guest.issuer}")
    private String guestIssuer;
    @Value("${spring.security.oauth2.guest.jwk-uri}")
    private String guestJwkUri;
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 ->
                oauth2.authenticationManagerResolver(
                        new CustomAuthenticationManagerResolver(
                                new CustomJwtAuthenticationConverter(),
                                new GoogleJwtAuthenticationConverter(),
                                new GuestJwtAuthenticationConverter(),
                                googleIssuer, googleJwkUri,
                                imageProcessingIssuer, imageProcessingJwkUri,
                                guestIssuer, guestJwkUri
                        )
                )
        );

        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of(allowedOrigins));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                return corsConfiguration;
            };
            c.configurationSource(source);
        });

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/auth/guest", "/api/v1/auth/guest").permitAll()

                .requestMatchers("/", "/index.html", "/public/**", "/assets/**").permitAll()

                .anyRequest().authenticated()
        );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
