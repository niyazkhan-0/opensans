package com.example.UploadService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaUploadedEvent {
    private String mediaId;
    private String objectKey;
    private String bucketName;
    private String contentType;
    private String uploadedBy;
    private String preSignedUrl;
}