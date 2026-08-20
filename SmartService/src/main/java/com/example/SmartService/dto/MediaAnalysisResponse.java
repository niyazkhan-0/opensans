package com.example.SmartService.dto;

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
public class MediaAnalysisResponse {

    private String topic;

    private String mediaId;

    @Builder.Default
    private Map<String, Object> details = new HashMap<>();

    @Builder.Default
    private List<String> summary = new ArrayList<>();
}
