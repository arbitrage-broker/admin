package com.arbitragebroker.admin.config;

import com.arbitragebroker.admin.enums.RoleType;
import com.arbitragebroker.admin.util.SessionHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static com.arbitragebroker.admin.enums.RoleType.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SessionHolder sessionHolder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HoneypotAuthenticationFilter honeypotFilter =
                new HoneypotAuthenticationFilter("website", sessionHolder);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(honeypotFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Static resources
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // Public endpoints
                        .requestMatchers("/", "/login", "/actuator/**").permitAll()
                        // Protected endpoints
                        .requestMatchers("/pnotify/**","/bootstrap-3.3.7/**","/logout","/access-denied","/notfound","/login","/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/**","/api/v1/parameter/**","/api/v1/parameter-group/**").hasAnyRole(RoleType.name(ADMIN), RoleType.name(SUPER_WISER), RoleType.name(MANAGER))
                        .requestMatchers("/dashboard","/wallet","/coin","/exchange","/questionAnswer","/notification","/fileUpload/**","/subscription","/subscriptionPackage","/api/v1/coin/**","/api/v1/exchange/**","/api/v1/role/**","/api/v1/wallet/**","/api/v1/notification/**","/api/v1/question/**","/api/v1/answer/**","/api/v1/subscription-package/**","/api/v1/subscription-package-detail/**","/api/v1/subscription/**","/api/v1/files/**","/api/v1/common/**","/api/v1/arbitrage/**").hasAnyRole(RoleType.name(ADMIN), RoleType.name(RoleType.MANAGER), RoleType.name(RoleType.SUPER_WISER))
                        .requestMatchers("/**").hasRole(RoleType.name(ADMIN))
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?errorMsg=invalidUserNameOrPassword")
                        .defaultSuccessUrl("/dashboard",true)
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("SESSION","JSESSIONID")
                )
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (isAjaxRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().write("Unauthorized");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (isAjaxRequest(request)) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.getWriter().write("Access Denied");
                            } else {
                                response.sendRedirect("/page_403");
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(3)
                        .expiredUrl("/login?errorMsg=sessionExpired")
                        .sessionRegistry(sessionRegistry())
                );

        return http.build();
    }
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest");
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}