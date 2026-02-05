package com.ip.authorizationserver.config;

import com.ip.authorizationserver.security.SecurityUsers;
import com.ip.authorizationserver.utils.Key;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class OAuth2SecurityConfig {
    @Value("${spring.security.oauth2.client.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.key.public-key}")
    private String publicKeyBase64;

    @Value("${spring.security.oauth2.key.private-key}")
    private String privateKeyBase64;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    private final Key keyUtil;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigure = new OAuth2AuthorizationServerConfigurer();

        http.securityMatcher(authorizationServerConfigure.getEndpointsMatcher())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServerConfigure.getEndpointsMatcher()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .with(authorizationServerConfigure, authorizationServer -> authorizationServer
                        .oidc(Customizer.withDefaults()));

        http.cors(c -> c.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(c -> c.loginPage("/login").permitAll())
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/api/v1/auth/guest").permitAll()
                        .requestMatchers("/login", "/login.html", "/logout").permitAll()
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                        .anyRequest().authenticated()
                );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                }));

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout", "/api/v1/users/**", "/api/v1/auth/guest")
        );

        http.cors(c -> c.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of(allowedOrigins));
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        };
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .scope(OidcScopes.OPENID)
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(24)).build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAPublicKey publicKey = keyUtil.loadPublicKey("public.pem");
        RSAPrivateKey privateKey = keyUtil.loadPrivateKey("private.pem");

        // Build JWK
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if ("access_token".equals(context.getTokenType().getValue())) {
                Authentication authentication = context.getPrincipal();
                Object principalObj = authentication.getPrincipal();

                if (principalObj instanceof SecurityUsers securityUser) {
                    context.getClaims().subject(securityUser.getId().toString());
                    context.getClaims().claim("user_id", securityUser.getId());
                    context.getClaims().claim("email", securityUser.getEmail());
                    context.getClaims().claim("roles", securityUser.getAuthorities());
                    context.getClaims().claim("name", securityUser.getName());
                }
            }
        };
    }
}
