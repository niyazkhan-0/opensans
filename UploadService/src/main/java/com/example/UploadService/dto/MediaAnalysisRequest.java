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
public class MediaAnalysisRequest {

    private String topic;

    private Map<String, Object> details = new HashMap<>();

    private List<String> summary = new ArrayList<>();
}
