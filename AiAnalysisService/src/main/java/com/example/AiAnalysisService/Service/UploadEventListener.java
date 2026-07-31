package com.example.AiAnalysisService.Service;

import com.example.AiAnalysisService.Model.AnalysisStatus;
import com.example.AiAnalysisService.Model.MediaAnalysisRequest;
import com.example.AiAnalysisService.Model.MediaUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class UploadEventListener {

    private final AiAnalysisService aiAnalysisService;
    private final UploadAnalysisService uploadAnalysisService;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "uploads-processor-group")
    public void processMediaUpload(MediaUploadedEvent uploadedEvent){
        log.info("Uploads received : " + uploadedEvent.getMediaId());
        log.info("pre signed url : " + uploadedEvent.getPreSignedUrl());
        log.info("Mime Type : " + uploadedEvent.getContentType() );


        aiAnalysisService.getAnalysis(uploadedEvent)
                .flatMap(analysisRequest -> {
                    analysisRequest.setMediaId(uploadedEvent.getMediaId());
                    log.info("Analysis request for media id : {}", analysisRequest.getMediaId() );
                    return uploadAnalysisService.uploadAnalysis(analysisRequest, AnalysisStatus.COMPLETED);
                })
                .doOnNext(response -> {
                    log.info("UploadAnalysis response : " + response);
                })
                .onErrorResume(error -> {
                    log.error("Failed to process upload event for media ID: {}. Reason: {}", uploadedEvent.getMediaId(), error.getMessage());

                    MediaAnalysisRequest failedRequest = MediaAnalysisRequest.builder()
                            .mediaId(uploadedEvent.getMediaId())
                            .build();

                    return uploadAnalysisService.uploadAnalysis(failedRequest, AnalysisStatus.FAILED);

                })
                .doOnError(ofFallbackError -> {
                    log.error("Failed to update status to FAILED for media ID: {}", uploadedEvent.getMediaId());
                })
                .onErrorComplete()
                .block();
    }
}
