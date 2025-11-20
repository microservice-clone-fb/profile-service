package com.tam.profile.configuration;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? uri : uri + "?" + queryString;

        log.info("🌐 Incoming Request: {} {} - From: {}", method, fullUrl, request.getRemoteAddr());

        // Log headers nếu cần
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            log.debug(
                    "🔑 Authorization header present: {}",
                    authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("❌ Error processing request: {} {} - Error: ", method, fullUrl, e);
            throw e;
        } finally {
            log.info("✅ Request completed: {} {} - Status: {}", method, fullUrl, response.getStatus());
        }
    }
}
