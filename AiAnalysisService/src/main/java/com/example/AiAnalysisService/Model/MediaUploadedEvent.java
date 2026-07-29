package com.example.AiAnalysisService.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadedEvent {

    private String mediaId;
    private String objectKey;
    private String bucketName;
    private String contentType;
    private String uploadedBy;
    private String preSignedUrl;
}