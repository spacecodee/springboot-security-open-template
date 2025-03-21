package com.spacecodee.springbootsecurityopentemplate.language;

import com.spacecodee.springbootsecurityopentemplate.exceptions.util.MessageParameterUtil;
import com.spacecodee.springbootsecurityopentemplate.language.constant.LanguageConstants;
import com.spacecodee.springbootsecurityopentemplate.utils.LocaleUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
@Slf4j
@RequiredArgsConstructor
public class MessageUtilComponent {

    private final MessageSource messageSource;
    private static final Map<String, Locale> localeCache = new ConcurrentHashMap<>();
    private final LocaleUtils localeUtils;

    public String getMessage(String message) {
        var locale = localeUtils.getCurrentLocale();

        this.validateMessage(message);
        try {
            Locale resolvedLocale = this.getLocaleApp(locale);
            String resolvedMessage = getMessageSource().getMessage(message, null, resolvedLocale);
            log.debug("Message resolved: key='{}', locale='{}', result='{}'",
                    message, locale, resolvedMessage);
            return resolvedMessage;
        } catch (Exception e) {
            handleMessageError(message, e);
            return message;
        }
    }

    public String getMessage(String message, Object... args) {
        var locale = localeUtils.getCurrentLocale();

        this.validateMessage(message);
        try {
            log.debug("Resolving parameterized message: key='{}', locale='{}', args='{}'",
                    message, locale, Arrays.toString(args));

            Object[] validatedArgs = MessageParameterUtil.validateAndCleanParams(args);
            String resolvedMessage = getMessageSource().getMessage(message, validatedArgs, getLocaleApp(locale));

            log.debug("Parameterized message resolved: '{}'", resolvedMessage);
            return resolvedMessage;
        } catch (Exception e) {
            handleMessageError(message, e);
            return message;
        }
    }

    public String getMessage(String message, String locale, Object... args) {

        this.validateMessage(message);
        try {

            Object[] validatedArgs = MessageParameterUtil.validateAndCleanParams(args);

            return getMessageSource().getMessage(message, validatedArgs, getLocaleApp(locale));
        } catch (Exception e) {
            handleMessageError(message, e);
            return message;
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message key cannot be null or empty");
        }
    }

    private void handleMessageError(String message, @NotNull Exception e) {
        var locale = localeUtils.getCurrentLocale();
        log.error("Error resolving message: key='{}', locale='{}', error='{}'",
                message, locale, e.getMessage());
    }

    private Locale getLocaleApp(String locale) {
        if (locale == null || locale.isEmpty()) {
            log.debug("Empty locale, using default: {}", LanguageConstants.DEFAULT_LOCALE);
            return Locale.forLanguageTag(LanguageConstants.DEFAULT_LOCALE);
        }

        return localeCache.computeIfAbsent(locale.toLowerCase(), key -> {
            log.debug("Computing locale for key: {}", key);
            try {
                var loc = Locale.forLanguageTag(key);
                if (isSupportedLocale(loc)) {
                    log.debug("Using supported locale: {}", loc);
                    return loc;
                }
            } catch (Exception e) {
                log.warn("Invalid locale: {}. Using default locale.", key);
            }
            log.debug("Falling back to default locale: {}", LanguageConstants.DEFAULT_LOCALE);
            return Locale.forLanguageTag(LanguageConstants.DEFAULT_LOCALE);
        });
    }

    public boolean isSupportedLocale(@NotNull Locale locale) {
        return Arrays.asList("en", "es").contains(locale.getLanguage());
    }

}
