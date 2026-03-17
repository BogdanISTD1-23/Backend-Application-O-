package com.operator.subscriber.service;

import com.operator.subscriber.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class PhotoStorageService {
    private final Path baseDir;

    public PhotoStorageService(@Value("${app.photo.upload-dir:uploads/photos}") String uploadDir) {
        this.baseDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String saveSubscriberPhoto(long subscriberId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Photo file is required");
        }

        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        String safeExt = (ext == null || ext.isBlank()) ? "bin" : ext.replaceAll("[^A-Za-z0-9]", "");

        Path dir = baseDir.resolve(Long.toString(subscriberId));
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(UUID.randomUUID() + "." + safeExt).normalize();
            if (!target.startsWith(dir)) {
                throw new BadRequestException("Invalid file path");
            }
            file.transferTo(target);
            return baseDir.relativize(target).toString().replace('\\', '/');
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save photo", e);
        }
    }

    public Resource loadAsResource(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new BadRequestException("Photo not set");
        }
        Path resolved = baseDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new BadRequestException("Invalid photo path");
        }
        FileSystemResource r = new FileSystemResource(resolved.toFile());
        if (!r.exists() || !r.isReadable()) {
            throw new BadRequestException("Photo not found");
        }
        return r;
    }

    public String detectContentType(Resource resource) {
        try {
            return Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            return null;
        }
    }
}

