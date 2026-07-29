package com.example.AiAnalysisService.Model;

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

    @Builder.Default
    private Map<String, Object> details = new HashMap<>();

    @Builder.Default
    private List<String> summary = new ArrayList<>();
}
