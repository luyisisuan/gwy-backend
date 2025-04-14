package com.example.gwy_backend.service.impl; // 或 service.storage.impl

import com.example.gwy_backend.service.FileStorageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // 用于读取配置
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct; // 用于初始化目录
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // 用于生成唯一文件名

@Service // 标记为 Bean
public class LocalStorageStrategy implements FileStorageStrategy {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageStrategy.class);

    // 从 application.properties 读取文件存储根目录
    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct // 在 Bean 初始化后执行
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(this.uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation); // 确保目录存在
            log.info("File storage location initialized at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not create the directory where the uploaded files will be stored.", ex);
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        // 清理文件名，防止路径遍历攻击
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            // 获取文件扩展名
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // 生成唯一文件名 (UUID + 扩展名)
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 确定目标存储路径 (包含子目录)
            Path targetDirectory = this.fileStorageLocation;
            if (StringUtils.hasText(subDirectory)) {
                targetDirectory = this.fileStorageLocation.resolve(StringUtils.cleanPath(subDirectory));
                Files.createDirectories(targetDirectory); // 确保子目录存在
            }
            Path targetLocation = targetDirectory.resolve(uniqueFilename);

            // 将文件复制到目标位置
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("Stored file {} to {}", uniqueFilename, targetLocation);
                // 返回用于访问的标识符 (包含子目录和唯一文件名)
                return (StringUtils.hasText(subDirectory) ? subDirectory + "/" : "") + uniqueFilename;
            }
        } catch (IOException ex) {
            log.error("Could not store file {}. Please try again!", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        } catch (Exception ex) {
            log.error("An unexpected error occurred storing file {}", originalFilename, ex);
            throw new RuntimeException("Could not store file " + originalFilename, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileIdentifier, String subDirectory) {
        try {
            // **注意:** fileIdentifier 现在包含了 subDirectory (如果存在)
            // Path filePath = getFilePath(fileIdentifier, subDirectory); // 调用下面的方法获取路径
            Path filePath = this.fileStorageLocation.resolve(fileIdentifier).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("File not found or not readable: {}", filePath);
                throw new RuntimeException("File not found " + fileIdentifier);
            }
        } catch (MalformedURLException ex) {
            log.error("Error creating URL for file: {}", fileIdentifier, ex);
            throw new RuntimeException("File path error " + fileIdentifier, ex);
        }
    }

    @Override
    public void deleteFile(String fileIdentifier, String subDirectory) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileIdentifier).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted file: {}", filePath);
            } else {
                log.warn("File not found, could not delete: {}", filePath);
            }
        } catch (IOException ex) {
            log.error("Could not delete file {}. Please try again!", fileIdentifier, ex);
            throw new RuntimeException("Could not delete file " + fileIdentifier, ex);
        }
    }

    // 可选: 获取文件路径的方法
    @Override
    public Path getFilePath(String fileIdentifier, String subDirectory) {
        // **注意:** fileIdentifier 现在应包含 subDirectory
        // Path directory = this.fileStorageLocation;
        // if (StringUtils.hasText(subDirectory)) {
        //     directory = directory.resolve(StringUtils.cleanPath(subDirectory));
        // }
        // return directory.resolve(fileIdentifier).normalize();
        return this.fileStorageLocation.resolve(fileIdentifier).normalize();
    }
}