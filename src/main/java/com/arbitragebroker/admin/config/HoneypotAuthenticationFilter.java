package com.arbitragebroker.admin.config;

import com.arbitragebroker.admin.util.SessionHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class HoneypotAuthenticationFilter extends OncePerRequestFilter {

    private final String honeypotFieldName;
    private final SessionHolder sessionHolder;

    public HoneypotAuthenticationFilter(String honeypotFieldName,SessionHolder sessionHolder) {
        this.honeypotFieldName = honeypotFieldName;
        this.sessionHolder = sessionHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ipAddress = getClientIp(request);
        MDC.put("clientIp", ipAddress);

        var user = sessionHolder.getCurrentUser();
        if(user != null)
            MDC.put("userId", user.getId().toString());

        if (isLoginRequest(request)) {
            String honeypotValue = request.getParameter(honeypotFieldName);
            if (honeypotValue != null && !honeypotValue.isEmpty()) {
                // Bot detected - redirect to login page with error
                response.sendRedirect("/login?errorMsg=botDetected");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getMethod().equals("POST") &&
                request.getServletPath().equals("/login");
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs in X-Forwarded-For, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}