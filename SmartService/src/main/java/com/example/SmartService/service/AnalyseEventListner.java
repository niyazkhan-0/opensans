package com.example.SmartService.service;

import com.example.SmartService.model.MediaAnalysisStoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnalyseEventListner {

    private final VectorStoreService vectorStoreService;

    @KafkaListener(topics = "${kafka.topic.analysis-stored}", groupId = "smart-service-group")
    public void processAnalysesEvent(MediaAnalysisStoreEvent mediaAnalysisStoreEvent){
        log.info("topic received : " + mediaAnalysisStoreEvent.getMediaId());
        vectorStoreService.storeVectorAnalysis(mediaAnalysisStoreEvent);
    }
}
