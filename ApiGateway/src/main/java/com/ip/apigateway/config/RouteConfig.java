package com.ip.apigateway.config;

import com.ip.apigateway.filter.AuthenticationHeaderFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {
    private final AuthenticationHeaderFilter authenticationHeaderFilter;

    //@Bean

}
