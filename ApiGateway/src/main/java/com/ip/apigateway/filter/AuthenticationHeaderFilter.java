package com.ip.apigateway.filter;

import com.ip.apigateway.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException; // Dùng cái này để chặn request
import org.springframework.web.servlet.function.ServerRequest;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class AuthenticationHeaderFilter {

    private static final Logger logger = Logger.getLogger(AuthenticationHeaderFilter.class.getName());
    private final RedisTemplate<String, String> redisTemplate;
    private final UserClient userClient;

    public Function<ServerRequest, ServerRequest> addAuthenticationHeader() {
        return request -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                logger.info("MISSING CREDENTIAL");
                return request;
            }

            String userId = authentication.getName();

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_ANONYMOUS");

            if (!"ROLE_GUEST".equals(role)) {
                if (!isUserActive(userId)) {
                    logger.warning("USER BANNED OR INACTIVE: " + userId);
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is banned or inactive");
                }
            }

            logger.info("VALID REQUEST (" + role + "), PASSING REQUEST TO DOWNSTREAM SERVICE");

            return ServerRequest.from(request)
                    .header("X-USER-ID", userId)
                    .header("X-USER-ROLE", role)
                    .build();
        };
    }

    private boolean isUserActive(String userId) {
        try {
            String userStatus = redisTemplate.opsForValue().get("user_status:" + userId); // Nên thêm prefix cho key redis đỡ trùng

            if (userStatus == null) {
                logger.info("CACHE MISS, CALLING USER SERVICE");
                userStatus = userClient.findUserStatus(userId);
                redisTemplate.opsForValue().set("user_status:" + userId, userStatus, 10, TimeUnit.MINUTES);
            }

            return !"BANNED".equals(userStatus);
        } catch (Exception e) {
            logger.severe("ERROR CHECKING USER STATUS: " + e.getMessage());
            return false;
        }
    }
}