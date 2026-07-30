package com.example.UploadService;

import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.UploadedMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<UploadedMedia, String> {

    @Modifying
    @Query("""
            Update UploadedMedia m
            SET m.analysisStatus = :analysisStatus
            WHERE m.id = :mediaId
            """)
    int setAnalysisStatus(@Param("analysisStatus")AnalysisStatus analysisStatus,
                          @Param("mediaId")String mediaId);
}
