package com.example.UploadService.service;

import com.example.UploadService.MediaAnalysisRepository;
import com.example.UploadService.MediaRepository;
import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.MediaAnalysis;
import com.example.UploadService.Model.UploadedMedia;
import com.example.UploadService.dto.MediaAnalysisRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MediaAnalysisRepository analysisRepository;
    private final MediaRepository mediaRepository;

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

        return "Analysis stored successfully";
    }


    public String updateAnalysisStatus(AnalysisStatus analysisStatus, String mediaId) {

        int updatedRows = mediaRepository.setAnalysisStatus(analysisStatus, mediaId);

        if(updatedRows == 0){
            throw new RuntimeException("No media found");
        }

        return "Media updated successfully";
    }
}
