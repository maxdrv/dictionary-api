package com.home.dictionary.config;


import com.home.dictionary.logging.LoggableDispatcherServlet;
import com.home.dictionary.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
public class AppConfig {

    @Autowired
    private CorsConfigurationProperties corsConfigurationProperties;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/api/v1/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedOrigins(corsConfigurationProperties.getAllowedOrigins())
                        .allowedHeaders("Origin", "Content-Type", "X-Auth-Token", "Authorization")
                        .allowCredentials(true)
                        ;

                registry
                        .addMapping("/api/v1/admin/user")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedOrigins(corsConfigurationProperties.getAllowedOrigins())
                        .allowedHeaders("Origin", "Content-Type", "X-Auth-Token", "Authorization")
                        .allowCredentials(true)
                ;

                registry
                        .addMapping("/ping")
                        .allowedMethods("GET")
                        .allowedOrigins(corsConfigurationProperties.getAllowedOrigins());
            }
        };
    }

    // log requests
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    @Bean
    public ServletRegistrationBean dispatcherRegistration() {
        return new ServletRegistrationBean(dispatcherServlet());
    }

    // log responses
    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new LoggableDispatcherServlet();
    }

    @Bean
    public Clock clock() {
        return Clock.system(DateTimeUtil.MOSCOW_ZONE_ID);
    }

}
