package io.github.legnatsol.springboot.ddd.util;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * SpringUtils
 */
@Component
public class SpringUtils {
    private static ApplicationContext applicationContext;

    /**
     * Constructor with application context
     *
     * @param context application context
     */
    public SpringUtils(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * publish event
     *
     * @param event event
     */
    public static void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }
}
