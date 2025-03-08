package com.arbitragebroker.admin.config;

import com.arbitragebroker.admin.dto.UserDetailDto;
import com.arbitragebroker.admin.enums.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuccessLoginConfig implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy;

    public SuccessLoginConfig() {
        this.redirectStrategy = new DefaultRedirectStrategy();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetailDto user = (UserDetailDto) authentication.getCredentials();
        MDC.put("userId", user.getId().toString());

        SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "");
        String targetUrl = "/dashboard";
        if (requestWrapper.isUserInRole(RoleType.USER))
            targetUrl = "/access-denied";
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}
