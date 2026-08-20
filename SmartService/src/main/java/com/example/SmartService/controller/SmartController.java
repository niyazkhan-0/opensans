package com.example.SmartService.controller;

import com.example.SmartService.dto.MediaAnalysisResponse;
import com.example.SmartService.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smart")
@RequiredArgsConstructor
public class SmartController {

    private final VectorStoreService vectorStoreService;

    @PostMapping("/search")
    public ResponseEntity<List<MediaAnalysisResponse>> search(@RequestParam("q") String query) {
        List<MediaAnalysisResponse> results = vectorStoreService.semanticSearch(query);
        return ResponseEntity.ok(results);
    }
}
