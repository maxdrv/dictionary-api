package com.home.dictionary.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.home.dictionary.openapi.model.CreatePhraseRequest;
import com.home.dictionary.openapi.model.LoginRequest;
import com.home.dictionary.openapi.model.RegisterRequest;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ApiCaller {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ApiCaller(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    public SneakyResultActions getPageOfPhrase(String params, Header... headers) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/phrase" + params)
                                .headers(toHttpHeaders(headers))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions postPhrase(CreatePhraseRequest request, Header... headers) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/phrase")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .headers(toHttpHeaders(headers))
                                .content(objectMapper.writeValueAsString(request))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions register(RegisterRequest request, Header... headers) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .headers(toHttpHeaders(headers))
                                .content(objectMapper.writeValueAsString(request))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions login(LoginRequest request) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(objectMapper.writeValueAsString(request))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions refresh(Header... headers) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/auth/refresh")
                                .headers(toHttpHeaders(headers))
                )
        );
    }

    @SneakyThrows
    public SneakyResultActions logout(Header... headers) {
        return new SneakyResultActions(
                mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/auth/logout")
                                .headers(toHttpHeaders(headers))
                )
        );
    }

    private static HttpHeaders toHttpHeaders(Header... headers) {
        var httpHeaders = new HttpHeaders();
        for (Header header : headers) {
            httpHeaders.add(header.name(), header.value());
        }
        return httpHeaders;
    }

}
