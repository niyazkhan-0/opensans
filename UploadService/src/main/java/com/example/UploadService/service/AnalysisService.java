package com.example.UploadService.service;

import com.example.UploadService.MediaAnalysisRepository;
import com.example.UploadService.MediaRepository;
import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.MediaAnalysis;
import com.example.UploadService.Model.UploadedMedia;
import com.example.UploadService.dto.MediaAnalysisRequest;
import com.example.UploadService.dto.MediaAnalysisResponse;
import com.example.UploadService.dto.MediaAnalysisStoreEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MediaAnalysisRepository analysisRepository;
    private final MediaRepository mediaRepository;
    private final KafkaTemplate<String, MediaAnalysisStoreEvent> kafkaTemplate;

    @Value("${kafka.topic.analysis-stored}")
    private String topicName;

    @Transactional
    public String storeAnalysis(MediaAnalysisRequest analysisRequest, AnalysisStatus status) {

        UploadedMedia uploadedMedia = mediaRepository
                .findById(analysisRequest.getMediaId())
                .orElseThrow(() -> new RuntimeException("Media not found"));

        MediaAnalysis mediaAnalysis = MediaAnalysis.builder()
                .media(uploadedMedia)
                .topic(analysisRequest.getTopic())
                .details(analysisRequest.getDetails())
                .summary(analysisRequest.getSummary())
                .build();

        uploadedMedia.setMediaAnalysis(mediaAnalysis);
        uploadedMedia.setAnalysisStatus(status);

        mediaRepository.save(uploadedMedia);

        //code for sending data to smart-service

        MediaAnalysisStoreEvent mediaAnalysisStoreEvent = MediaAnalysisStoreEvent.builder()
                .mediaId(uploadedMedia.getId())
                .uploadedBy(uploadedMedia.getUploadedBy())
                .topic(mediaAnalysis.getTopic())
                .details(mediaAnalysis.getDetails())
                .summary(mediaAnalysis.getSummary())
                .build();

        kafkaTemplate.send(topicName, uploadedMedia.getId(),mediaAnalysisStoreEvent);


        return "Analysis stored successfully";
    }


    public String updateAnalysisStatus(AnalysisStatus analysisStatus, String mediaId) {

        int updatedRows = mediaRepository.setAnalysisStatus(analysisStatus, mediaId);

        if(updatedRows == 0){
            throw new RuntimeException("No media found");
        }

        return "Media updated successfully";
    }

    public MediaAnalysisResponse getMediaAnalysis(String id) {

        if(id == null){
            throw new RuntimeException("invalid ID");
        }

        MediaAnalysis mediaAnalysis = analysisRepository.findByMediaId(id)
                .orElseThrow(() -> new RuntimeException("Medial not found with the analysis: " + id));

        return MediaAnalysisResponse.builder()
                .topic(mediaAnalysis.getTopic())
                .summary(mediaAnalysis.getSummary())
                .details(mediaAnalysis.getDetails())
                .build();
    }
}
