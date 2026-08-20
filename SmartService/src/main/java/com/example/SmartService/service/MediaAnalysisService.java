package com.example.SmartService.service;

import com.example.SmartService.dto.MediaAnalysisResponse;
import com.example.SmartService.dto.MultipleMediaAnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaAnalysisService {

    private final WebClient uploadServiceWebClient;

    public List<MediaAnalysisResponse> getAnalysis(MultipleMediaAnalysisRequest mediaIds){
        return uploadServiceWebClient.post()
                .uri("/api/media/analysis/batch")
                .bodyValue(mediaIds)
                .retrieve()
                .bodyToFlux(MediaAnalysisResponse.class)
                .collectList()
                .block();
    }
}
