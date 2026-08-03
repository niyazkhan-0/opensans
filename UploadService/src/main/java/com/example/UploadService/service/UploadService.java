package com.example.UploadService.service;

import com.example.UploadService.MediaRepository;
import com.example.UploadService.Model.AnalysisStatus;
import com.example.UploadService.Model.ContentType;
import com.example.UploadService.Model.UploadStatus;
import com.example.UploadService.Model.UploadedMedia;
import com.example.UploadService.dto.MediaUploadedEvent;
import com.example.UploadService.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final MinioService minioService;
    private final MediaRepository mediaRepository;
    private final KafkaTemplate<String, MediaUploadedEvent> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public UploadResponse uploadMedia(MultipartFile file, String userId) {
        // 1. Validate file
        if(file.isEmpty()){
            throw new IllegalArgumentException("File cannot be empty");
        }

        // 2. Generate unique object key
        String extention = "bin";
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            extention = FileNameUtils.getExtension(filename);
        }
        String objectKey = UUID.randomUUID() + "." + extention;

        // 3. Upload to MinIO
        String bucketName = minioService.uploadFile(file, objectKey);

        String checksum;
        try{
            checksum = DigestUtils.md5DigestAsHex(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }


        // 5. Save metadata
        UploadedMedia uploadedMedia = mediaRepository.save(UploadedMedia.builder()
                .uploadedBy(userId)
                .objectKey(objectKey)
                .filename(file.getOriginalFilename())
                .bucketName(bucketName)
                .fileSize(file.getSize())
                .checksum(checksum)
                .contentType(getContentType(file.getContentType()))
                .uploadStatus(UploadStatus.UPLOADED)
                .analysisStatus(AnalysisStatus.PENDING)
                .build());

        MediaUploadedEvent mediaEvent = MediaUploadedEvent.builder()
                .mediaId(uploadedMedia.getId())
                .objectKey(uploadedMedia.getObjectKey())
                .bucketName(uploadedMedia.getBucketName())
                .contentType(file.getContentType())
                .uploadedBy(uploadedMedia.getUploadedBy())
                .preSignedUrl(minioService.getUrl(uploadedMedia.getObjectKey()))
                .build();

        // 6. Publish Kafka Event
        try{
            kafkaTemplate.send(topicName, mediaEvent.getMediaId(), mediaEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 7. Return UploadResponse

        return UploadResponse.builder()

                .id(UUID.fromString(uploadedMedia.getId()))

                .fileName(uploadedMedia.getFilename())

                .objectKey(uploadedMedia.getObjectKey())

                .contentType(String.valueOf(uploadedMedia.getContentType()) )

                .fileSize(uploadedMedia.getFileSize())

                .uploadStatus(uploadedMedia.getUploadStatus())

                .analysisStatus(uploadedMedia.getAnalysisStatus())

                .build();
    }

    private ContentType getContentType(String mimeType) {

        if (mimeType == null) {
            throw new IllegalArgumentException("Unknown content type");
        }

        if (mimeType.startsWith("image/")) {
            return ContentType.IMAGE;
        }

        if (mimeType.startsWith("video/")) {
            return ContentType.VIDEO;
        }

        if (mimeType.startsWith("audio/")) {
            return ContentType.AUDIO;
        }

        throw new IllegalArgumentException("Unsupported file type");
    }

    public void deleteMedia(String id) {

        UploadedMedia media = mediaRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No media found"));

        mediaRepository.delete(media);
    }
}
