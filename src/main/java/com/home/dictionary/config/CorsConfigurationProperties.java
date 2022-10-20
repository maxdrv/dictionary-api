package com.home.dictionary.config;

import lombok.Data;
import one.util.streamex.StreamEx;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("cors")
public class CorsConfigurationProperties {

    private List<String> allowOrigins;

    public String[] getAllowedOrigins() {
        return StreamEx.of(allowOrigins)
                .toArray(new String[]{});
    }

}
