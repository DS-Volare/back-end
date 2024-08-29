package com.example.volare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import com.example.volare.dto.StoryboardDTO;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryboardService {

    private final String aiServerUrl = "http://75.63.212.242:44809/convert_storyboard/";

    public StoryboardDTO.Response generateStoryboard(StoryboardDTO.Request request) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StoryboardDTO.Request> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<StoryboardDTO.Response> response = restTemplate.exchange(
                    aiServerUrl, HttpMethod.POST, entity, StoryboardDTO.Response.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Failed to generate storyboard: Status code - {}", response.getStatusCode());
                throw new RuntimeException("Failed to generate storyboard: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error: Status code - {}, Response body - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("HTTP error occurred while generating storyboard", e);
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new RuntimeException("An unexpected error occurred while generating storyboard", e);
        }
    }
}