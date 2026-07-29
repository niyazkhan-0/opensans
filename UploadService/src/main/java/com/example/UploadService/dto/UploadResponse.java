package com.example.UploadService.dto;


import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.UploadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
public class UploadResponse {

    private UUID id;

    private String fileName;

    private String objectKey;

    private String contentType;

    private Long fileSize;

    private UploadStatus uploadStatus;

    private AnalysisStatus analysisStatus;

}
