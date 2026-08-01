package com.example.UploadService.controller;

import com.example.UploadService.Model.MediaAnalysis;
import com.example.UploadService.dto.UploadResponse;
import com.example.UploadService.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadMedia(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-ID") String userId
    ){

        UploadResponse response = uploadService.uploadMedia(file, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping()
    public ResponseEntity<MediaAnalysis> getMediaAnalysis(){}

}
