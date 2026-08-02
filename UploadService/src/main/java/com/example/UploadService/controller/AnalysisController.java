package com.example.UploadService.controller;

import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.UploadedMedia;
import com.example.UploadService.dto.MediaAnalysisRequest;
import com.example.UploadService.dto.MediaAnalysisResponse;
import com.example.UploadService.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis/upload")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<String> uploadAnalysis(
            @RequestBody MediaAnalysisRequest analysisRequest,
            @RequestHeader("Analysis-Status")AnalysisStatus analysisStatus)
    {
        if(analysisStatus.equals(AnalysisStatus.COMPLETED)){
            return ResponseEntity.ok(analysisService.storeAnalysis(analysisRequest, analysisStatus));
        }else{
            return ResponseEntity.ok(analysisService.updateAnalysisStatus(analysisStatus, analysisRequest.getMediaId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaAnalysisResponse> getMediaAnalysis(
            @PathVariable String id
    ){
        MediaAnalysisResponse mediaAnalysisResponse= analysisService.getMediaAnalysis(id);

        return ResponseEntity.ok(mediaAnalysisResponse);
    }

}
