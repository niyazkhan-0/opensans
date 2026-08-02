package com.example.UploadService;


import com.example.UploadService.Model.MediaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaAnalysisRepository extends JpaRepository<MediaAnalysis, String> {

    Optional<MediaAnalysis> findByMediaId(String id);
}
