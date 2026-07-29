package com.example.UploadService;

import com.example.UploadService.Model.UploadedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<UploadedMedia, String> {
}
