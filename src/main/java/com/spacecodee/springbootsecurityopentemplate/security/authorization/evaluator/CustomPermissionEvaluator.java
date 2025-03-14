package com.spacecodee.springbootsecurityopentemplate.security.authorization.evaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.spacecodee.springbootsecurityopentemplate.service.security.user.IUserSecurityService;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final IUserSecurityService userSecurityService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        String targetType = targetDomainObject.getClass().getSimpleName().toLowerCase();
        return hasPrivilege(authentication, targetType, permission.toString().toLowerCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        if (authentication == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege(authentication, targetType.toLowerCase(), permission.toString().toLowerCase());
    }

    private boolean hasPrivilege(@NotNull Authentication auth, String targetType, String permission) {
        try {
            // Direct check against authorities
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().startsWith("ROLE_") &&
                            a.getAuthority().substring(5).toLowerCase().contains(targetType) &&
                            a.getAuthority().toLowerCase().contains(permission));
        } catch (Exception e) {
            log.error("Error checking privileges: {}", e.getMessage());
            return false;
        }
    }
}
