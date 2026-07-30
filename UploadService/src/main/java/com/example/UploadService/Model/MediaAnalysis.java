package com.example.UploadService.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "media_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", referencedColumnName = "id", nullable = false, unique = true)
    private UploadedMedia media;

    //ai response topic
    @Column(nullable = false, length = 255)
    private String topic;

    //ai response description
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> details = new HashMap<>();

    //ai response summary
    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> summary = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime analyzedAt;

    public void setMedia(UploadedMedia media) {

        this.media = media;

        if (media != null && media.getMediaAnalysis() != this) {
            media.setMediaAnalysis(this);
        }
    }

}