package com.example.empay.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A workaround to provide access to the Spring's ApplicationContext in a static fashion.
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    /**
     * Reference to the Spring application context.
     */
    private static ApplicationContext context;

    /**
     * Gets the Spring application context.
     * @return The Spring application context.
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * Set the Spring application context.
     * @param context the ApplicationContext object to be used by this object
     */
    public void setApplicationContext(final ApplicationContext context) {
        ApplicationContextProvider.context = context;
    }
}
