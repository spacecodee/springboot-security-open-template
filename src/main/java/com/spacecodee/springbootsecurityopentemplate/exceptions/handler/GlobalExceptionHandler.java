package com.spacecodee.springbootsecurityopentemplate.exceptions.handler;

import com.spacecodee.springbootsecurityopentemplate.data.common.response.ApiErrorDataPojo;
import com.spacecodee.springbootsecurityopentemplate.data.common.response.ApiErrorPojo;
import com.spacecodee.springbootsecurityopentemplate.exceptions.auth.InvalidCredentialsException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.auth.TokenExpiredException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.auth.UnauthorizedException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.base.BaseException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.endpoint.ModuleNotFoundException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.endpoint.OperationNotFoundException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.endpoint.PermissionNotFoundException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.operation.NoContentException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.user.LastAdminException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.user.LastDeveloperException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.user.LastTechnicianException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.user.UserNotFoundException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.validation.AlreadyExistsException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.validation.InvalidParameterException;
import com.spacecodee.springbootsecurityopentemplate.exceptions.validation.PasswordDoNotMatchException;
import com.spacecodee.springbootsecurityopentemplate.language.MessageUtilComponent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageUtilComponent messageUtilComponent;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorPojo> handleBusinessException(@NotNull BaseException ex, HttpServletRequest request) {
        log.error("Business exception occurred: {}", ex.getMessage());
        return ResponseEntity
                .status(determineHttpStatus(ex))
                .body(createErrorResponse(ex, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDataPojo<List<String>>> handleValidationException(
            @NotNull MethodArgumentNotValidException ex,
            @NotNull HttpServletRequest request) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorDataPojo.of(
                        ex.getLocalizedMessage(),
                        messageUtilComponent.getMessage("validation.error", "en"),
                        request.getRequestURI(),
                        request.getMethod(),
                        errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorPojo> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createGenericErrorResponse(ex, request));
    }

    private @NotNull ApiErrorPojo createErrorResponse(@NotNull BaseException ex, @NotNull HttpServletRequest request) {
        String userMessage = messageUtilComponent.getMessage(ex.getMessageKey(), ex.getLocale());
        String technicalDetails = String.format(
                "Exception: %s, Key: %s, Locale: %s, Path: %s, Method: %s",
                ex.getClass().getSimpleName(),
                ex.getMessageKey(),
                ex.getLocale(),
                request.getRequestURI(),
                request.getMethod());

        return ApiErrorPojo.of(
                technicalDetails,
                userMessage,
                request.getRequestURI(),
                request.getMethod());
    }

    private @NotNull ApiErrorPojo createGenericErrorResponse(@NotNull Exception ex,
                                                             @NotNull HttpServletRequest request) {
        return ApiErrorPojo.of(
                ex.getLocalizedMessage(),
                messageUtilComponent.getMessage("error.unexpected", "en"),
                request.getRequestURI(),
                request.getMethod());
    }

    @Contract(pure = true)
    private HttpStatus determineHttpStatus(@NotNull BaseException ex) {
        return switch (ex) {
            case LastAdminException ignored -> HttpStatus.CONFLICT;
            case LastTechnicianException ignored -> HttpStatus.CONFLICT;
            case LastDeveloperException ignored -> HttpStatus.CONFLICT;
            case InvalidCredentialsException ignored -> HttpStatus.UNAUTHORIZED;
            case TokenExpiredException ignored -> HttpStatus.UNAUTHORIZED;
            case UnauthorizedException ignored -> HttpStatus.FORBIDDEN;
            case UserNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case ModuleNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case OperationNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case PermissionNotFoundException ignored -> HttpStatus.NOT_FOUND;
            case AlreadyExistsException ignored -> HttpStatus.BAD_REQUEST;
            case InvalidParameterException ignored -> HttpStatus.BAD_REQUEST;
            case PasswordDoNotMatchException ignored -> HttpStatus.BAD_REQUEST;
            case NoContentException ignored -> HttpStatus.NO_CONTENT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
