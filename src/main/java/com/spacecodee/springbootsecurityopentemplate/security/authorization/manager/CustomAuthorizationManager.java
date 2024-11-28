package com.spacecodee.springbootsecurityopentemplate.security.authorization.manager;

import com.spacecodee.springbootsecurityopentemplate.data.dto.user.details.UserDetailsOperationDTO;
import com.spacecodee.springbootsecurityopentemplate.data.dto.user.details.UserDetailsPermissionDTO;
import com.spacecodee.springbootsecurityopentemplate.security.authentication.filter.LocaleResolverFilter;
import com.spacecodee.springbootsecurityopentemplate.service.core.endpoint.IOperationService;
import com.spacecodee.springbootsecurityopentemplate.service.core.user.details.IUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@AllArgsConstructor
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final IOperationService operationService;
    private final IUserDetailsService userService;

    private final Logger logger = Logger.getLogger(CustomAuthorizationManager.class.getName());

    @Override
    public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationManager.super.verify(authentication, object);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
            @NotNull RequestAuthorizationContext object) {

        HttpServletRequest request = object.getRequest();
        var url = this.extractUrl(request);
        var httpMethod = request.getMethod();

        var isPublic = this.isPublic(url, httpMethod);
        if (isPublic) {
            return new AuthorizationDecision(true);
        }

        var isGranted = this.isGranted(authentication.get(), url, httpMethod);
        return new AuthorizationDecision(isGranted);
    }

    private boolean isGranted(Authentication authentication, String url, String httpMethod) {
        if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication credentials weren't found.");
        }

        List<UserDetailsOperationDTO> operations = this.obtainOperations(authentication);
        return operations.stream()
                .anyMatch(CustomAuthorizationManager.getOperationDtoPredicate(url, httpMethod));
    }

    private List<UserDetailsOperationDTO> obtainOperations(Authentication authentication) {
        var authToken = (UsernamePasswordAuthenticationToken) authentication;
        var username = authToken.getPrincipal().toString();
        var locale = LocaleResolverFilter.getCurrentLocale();
        var user = this.userService.findByUsername(locale, username);

        return user.getUserDetailsRoleDTO().getUserDetailsPermissionDTOList().stream()
                .map(UserDetailsPermissionDTO::getOperationDTO).toList();
    }

    private boolean isPublic(String url, String httpMethod) {
        var publicAccessEndpoints = this.operationService.findByPublicAccess();
        return publicAccessEndpoints
                .stream()
                .anyMatch(CustomAuthorizationManager.getOperationDtoPredicate(url, httpMethod));
    }

    @Contract(pure = true)
    private static @NotNull Predicate<UserDetailsOperationDTO> getOperationDtoPredicate(String url, String httpMethod) {
        return operation -> {
            var basePath = operation.getModuleDTO().getBasePath();
            var pattern = Pattern.compile(basePath.concat(operation.getPath()));
            var matcher = pattern.matcher(url);
            return matcher.matches() && operation.getHttpMethod().equals(httpMethod);
        };
    }

    private @NotNull String extractUrl(@NotNull HttpServletRequest request) {
        var contextPath = request.getContextPath();
        var url = request.getRequestURI();
        url = url.replaceFirst(contextPath, "");

        return url;
    }
}