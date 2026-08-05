package com.example.UploadService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MediaAnalysisStoreEvent {

    private String mediaId;

    private String uploadedBy;

    private String topic;

    @Builder.Default
    private List<String> summary = new ArrayList<>();

    @Builder.Default
    private Map<String, Object> details = new HashMap<>();
}
