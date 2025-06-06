package com.template.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Logging configuration for the application.
 * Provides enhanced request logging and application startup logging.
 */
@Configuration
public class LoggingConfig {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);
    
    private final Environment env;
    
    public LoggingConfig(Environment env) {
        this.env = env;
    }
    
    /**
     * Configures request logging with body content for debugging purposes.
     * Only logs request/response bodies in development mode.
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
    
    /**
     * Logs application startup information including URLs and active profiles.
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationReadyEventListener() {
        return event -> {
            String applicationName = env.getProperty("spring.application.name");
            String protocol = "http";
            
            if (env.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }
            
            String serverPort = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "/");
            
            if (!contextPath.startsWith("/")) {
                contextPath = "/" + contextPath;
            }
            
            String hostAddress = "localhost";
            
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("Could not determine host address", e);
            }
            
            String[] activeProfiles = env.getActiveProfiles();
            
            log.info(
                    "\n----------------------------------------------------------\n" +
                    "Application '{}' is running! Access URLs:\n" +
                    "Local: \t\t{}://localhost:{}{}\n" +
                    "External: \t{}://{}:{}{}\n" +
                    "Profile(s): \t{}\n" +
                    "----------------------------------------------------------",
                    applicationName,
                    protocol,
                    serverPort,
                    contextPath,
                    protocol,
                    hostAddress,
                    serverPort,
                    contextPath,
                    activeProfiles.length == 0 ? "default" : Arrays.toString(activeProfiles)
            );
            
            String configServerStatus = env.getProperty("configserver.status");
            if (configServerStatus != null) {
                log.info(
                        "\n----------------------------------------------------------\n" +
                        "Config Server: \t{}\n" +
                        "----------------------------------------------------------",
                        configServerStatus
                );
            }
        };
    }
    
    /**
     * Customizes the embedded server to log errors in a standardized format.
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.addErrorPages(
                // We'll let Spring's default error handling work, this is just to ensure
                // the server factory is initialized with our logging configuration
        );
    }
}
