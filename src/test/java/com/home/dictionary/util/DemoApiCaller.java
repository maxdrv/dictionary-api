package com.home.dictionary.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.home.dictionary.openapi.model.CreateDemoDto;
import com.home.dictionary.openapi.model.UpdateDemoDto;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Component
public class DemoApiCaller {

    private static final String DEMO_URL = "/api/v1/demo";
    private static final String DEMO_ID_URL = "/api/v1/demo/{demoId}";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public DemoApiCaller(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    public SneakyResultActions page(String params) {
        return new SneakyResultActions(
                mockMvc.perform(get(DEMO_URL + params))
        );
    }

    @SneakyThrows
    public SneakyResultActions getById(Long demoId) {
        return new SneakyResultActions(
                mockMvc.perform(get(DEMO_ID_URL, demoId))
        );
    }

    @SneakyThrows
    public SneakyResultActions create(CreateDemoDto request) {
        return new SneakyResultActions(
                mockMvc.perform(
                        post(DEMO_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions update(Long demoId, UpdateDemoDto request) {
        return new SneakyResultActions(
                mockMvc.perform(
                        put(DEMO_ID_URL, demoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions delete(Long demoId) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(DEMO_ID_URL, demoId)
                )
        );
    }

}
