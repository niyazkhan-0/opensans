package com.example.UploadService;


import com.example.UploadService.Model.MediaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaAnalysisRepository extends JpaRepository<MediaAnalysis, String> {

    Optional<MediaAnalysis> findByMediaId(String id);

    List<MediaAnalysis> findByMediaIdIn(List<String> ids);
}
