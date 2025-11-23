package com.tam.profile.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String token = null;

        // Cách 1: Lấy từ ServletRequest (cho các call từ Controller)
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes != null) {
            var authHeader = servletRequestAttributes.getRequest().getHeader("Authorization");
            if (StringUtils.hasText(authHeader)) {
                token = authHeader;
                log.info("✅ Got token from ServletRequest");
            }
        }

        // Cách 2: Lấy từ SecurityContext (cho các call từ Service layer)
        if (token == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                token = "Bearer " + jwt.getTokenValue();
                log.info("✅ Got token from SecurityContext (JWT)");
            } else if (authentication != null && authentication.getCredentials() instanceof String credentials) {
                token = "Bearer " + credentials;
                log.info("✅ Got token from SecurityContext (credentials)");
            }
        }

        // Thêm token vào request
        if (StringUtils.hasText(token)) {
            template.header("Authorization", token);
            log.info("✅ Added Authorization header to Feign request");
        } else {
            log.warn("⚠️ No token found - request will be unauthenticated");
        }
    }
}
