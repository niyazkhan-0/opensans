package com.example.AiAnalysisService.Service;

import com.example.AiAnalysisService.Model.AnalysisStatus;
import com.example.AiAnalysisService.Model.MediaAnalysisRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UploadAnalysisService {

    private final WebClient uploadServiceWebClient;

    public Mono<String> uploadAnalysis(MediaAnalysisRequest analysisRequest, AnalysisStatus analysisStatus){
        return uploadServiceWebClient.post()
                .uri("/api/media/analysis")
                .bodyValue(analysisRequest)
                .header("Analysis-Status",String.valueOf(analysisStatus) )
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                        return Mono.error(new RuntimeException("Invalid request : " + analysisRequest.getMediaId()));
                    }

                    return Mono.error(new RuntimeException("Unexpected : " + analysisRequest.getMediaId()));
                });
    }
}
