package com.example.UploadService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(
            mappedBy = "media",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private MediaAnalysis mediaAnalysis;

    private String uploadedBy;

    private String objectKey;

    private String filename;

    private String bucketName;

    private Long fileSize;

    @Column(unique = true)
    private String checksum;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus analysisStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (analysisStatus == null) {
            analysisStatus = AnalysisStatus.PROCESSING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setMediaAnalysis(MediaAnalysis analysis) {

        this.mediaAnalysis = analysis;

        if (analysis != null && analysis.getMedia() != this) {
            analysis.setMedia(this);
        }
    }


}
