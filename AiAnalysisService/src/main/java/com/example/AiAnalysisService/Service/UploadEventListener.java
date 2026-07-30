package com.example.AiAnalysisService.Service;

import com.example.AiAnalysisService.Model.MediaAnalysisRequest;
import com.example.AiAnalysisService.Model.MediaUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UploadEventListener {

    private final AiAnalysisService analysisService;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "uploads-processor-group")
    public void processMediaUpload(MediaUploadedEvent uploadedEvent){
        log.info("Uploads received : " + uploadedEvent.getMediaId());
        log.info("pre signed url : " + uploadedEvent.getPreSignedUrl());
        log.info("Mime Type : " + uploadedEvent.getContentType() );

        analysisService.getAnalysis(uploadedEvent)
                .doOnNext(analysisRequest -> {
                    log.info("Analysis completed successfully for media ID: {}", uploadedEvent.getMediaId());
                    log.info("Analyzed Topic: {}", analysisRequest.getTopic());
                    log.info("Analyzed Details: {}", analysisRequest.getDetails());
                    log.info("Analyzed Summary: {}", analysisRequest.getSummary());

                })
                .doOnError(error -> {
                    log.error("Failed to process upload event: {}", error.getMessage());
                })
                .block();
    }
}
