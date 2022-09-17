package com.home.dictionary.service;

import com.home.dictionary.model.configuration.ApiProperty;
import com.home.dictionary.model.configuration.ApiPropertyKey;
import com.home.dictionary.repository.ApiPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ConfigurationService {

    private final ApiPropertyRepository apiPropertyRepository;

    public boolean registrationNotAllowed() {
        return !registrationAllowed();
    }

    public boolean registrationAllowed() {
        return apiPropertyRepository.findByKey(ApiPropertyKey.REGISTRATION_ALLOWED.name())
                .map(ApiProperty::getValue)
                .map(ConfigurationService::parseBoolean)
                .orElse(false);
    }

    private static boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return Boolean.parseBoolean(value.trim());
    }

}
