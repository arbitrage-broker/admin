package com.arbitragebroker.admin.enums;

import com.arbitragebroker.admin.util.MapperHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public class RoleType {
    private static String ADMIN_ID = "92d18767-6336-474d-9b57-9cec381db56b";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String MANAGER = "ROLE_MANAGER";
    public static final String SUPER_WISER = "ROLE_SUPER_WISER";
    public static final String USER = "ROLE_USER";

    public static String name(String role){
        return role.replace("ROLE_","");
    }
    public static boolean hasRole(String roleName) {
        var auth = MapperHelper.get(()->SecurityContextHolder.getContext().getAuthentication());
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
        }
        return false;
    }
    public static List<String> allRoles() {
        var auth = MapperHelper.get(()->SecurityContextHolder.getContext().getAuthentication());
        if (auth != null) {
            return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        }
        return null;
    }
    public static String firstRole() {
        var auth = MapperHelper.get(()->SecurityContextHolder.getContext().getAuthentication());
        if (auth != null) {
            return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null);
        }
        return null;
    }
}
