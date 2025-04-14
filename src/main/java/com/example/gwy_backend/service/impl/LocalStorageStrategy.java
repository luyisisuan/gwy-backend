package com.example.gwy_backend.service.impl; // 或 service.storage.impl

import com.example.gwy_backend.service.FileStorageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 使用本地文件系统实现的文件存储策略。
 */
@Service // 标记为 Bean，Spring 会自动发现并创建实例
public class LocalStorageStrategy implements FileStorageStrategy {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageStrategy.class);

    @Value("${file.upload-dir}") // 从配置文件读取上传根目录
    private String uploadDir;

    private Path fileStorageLocation; // 存储文件的根路径

    @PostConstruct // 初始化方法
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(this.uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation); // 确保目录存在
            log.info("Initialized file storage location at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not initialize storage location: {}", this.uploadDir, ex);
            throw new RuntimeException("Could not initialize storage location!", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 确定最终的存储目录 (根目录 + 子目录)
            Path targetDirectory = this.fileStorageLocation;
            String relativeSubDirectory = ""; // 用于返回的标识符
            if (StringUtils.hasText(subDirectory)) {
                relativeSubDirectory = StringUtils.cleanPath(subDirectory);
                targetDirectory = this.fileStorageLocation.resolve(relativeSubDirectory);
                Files.createDirectories(targetDirectory);
            }

            Path targetLocation = targetDirectory.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("Stored file {} to {}", uniqueFilename, targetLocation);
                 // 返回包含子目录的相对路径作为标识符
                return (StringUtils.hasText(relativeSubDirectory) ? relativeSubDirectory + "/" : "") + uniqueFilename;
            }
        } catch (IOException ex) {
            log.error("Could not store file {}. IO Error.", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename + ". IO Error.", ex);
        } catch (Exception ex) {
             log.error("An unexpected error occurred storing file {}", originalFilename, ex);
             throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileIdentifier, String subDirectoryIgnored) {
        try {
            // 直接使用包含子目录的 fileIdentifier 解析路径
            Path filePath = getFilePath(fileIdentifier, null); // 调用下面的 getFilePath
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("Attempted to load file but it was not found or not readable: {}", filePath);
                throw new RuntimeException("File not found: " + fileIdentifier);
            }
        } catch (MalformedURLException ex) {
            log.error("Malformed URL for file path: {}", fileIdentifier, ex);
            throw new RuntimeException("File path error: " + fileIdentifier, ex);
        } catch (Exception e) {
             log.error("Error loading file as resource: {}", fileIdentifier, e);
             throw new RuntimeException("Error loading file: " + fileIdentifier, e);
        }
    }

    @Override
    public void deleteFile(String fileIdentifier, String subDirectoryIgnored) {
         try {
             Path filePath = getFilePath(fileIdentifier, null);
             boolean deleted = Files.deleteIfExists(filePath);
             if (deleted) {
                  log.info("Deleted file: {}", filePath);
             } else {
                  log.warn("File not found, could not delete: {}", filePath);
             }
        } catch (IOException ex) {
            log.error("Could not delete file {}. IO Error.", fileIdentifier, ex);
            throw new RuntimeException("Could not delete file " + fileIdentifier + ". IO Error.", ex);
        } catch (Exception e) {
             log.error("Error deleting file: {}", fileIdentifier, e);
             throw new RuntimeException("Error deleting file: " + fileIdentifier, e);
        }
    }

    @Override
    public Path getFilePath(String fileIdentifier, String subDirectoryIgnored) {
         // fileIdentifier 已经包含了子目录 (e.g., "errors/uuid.jpg")
         if (!StringUtils.hasText(fileIdentifier)) {
              throw new IllegalArgumentException("File identifier cannot be empty.");
         }
         // 解析路径，并进行安全检查防止路径穿越
         Path resolvedPath = this.fileStorageLocation.resolve(StringUtils.cleanPath(fileIdentifier)).normalize();
         if (!resolvedPath.startsWith(this.fileStorageLocation)) {
              throw new RuntimeException("Cannot access files outside storage directory! " + fileIdentifier);
         }
         return resolvedPath;
    }
}