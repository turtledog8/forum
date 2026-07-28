package org.example.forum.service.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.example.forum.exception.InvalidFileException;
import org.example.forum.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path uploadRoot;

    public FileStorageServiceImpl(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("No file provided");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Only JPEG, PNG, GIF or WEBP images are allowed");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileException("Unsupported file extension: " + extension);
        }

        try {
            Path targetDir = uploadRoot.resolve(subDirectory).normalize();
            if (!targetDir.startsWith(uploadRoot)) {
                throw new InvalidFileException("Invalid target directory");
            }
            Files.createDirectories(targetDir);

            String filename = UUID.randomUUID() + "." + extension;
            Path target = targetDir.resolve(filename).normalize();
            if (!target.startsWith(targetDir)) {
                throw new InvalidFileException("Invalid file name");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subDirectory + "/" + filename;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file", e);
        }
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }
}
