package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.ErrorLogEntry; // 需要导入实体
import com.example.gwy_backend.entity.KnowledgeItem;
import com.example.gwy_backend.repository.ErrorLogEntryRepository; // 需要导入 Repository
import com.example.gwy_backend.repository.KnowledgeItemRepository;
import com.example.gwy_backend.service.FileStorageStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // 需要事务
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // 用于构建 URI

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map; // 用于接收简单响应

@RestController
@RequestMapping("/api/files") // 文件操作的基础路径
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileStorageStrategy fileStorageStrategy;
    // 注入 Repository 以更新文件路径到实体 (简化处理，更好的方式是在 Service 中处理)
    private final ErrorLogEntryRepository errorLogRepo;
    private final KnowledgeItemRepository knowledgeRepo;


    @Autowired
    public FileController(FileStorageStrategy fileStorageStrategy,
                          ErrorLogEntryRepository errorLogRepo,
                          KnowledgeItemRepository knowledgeRepo) {
        this.fileStorageStrategy = fileStorageStrategy;
        this.errorLogRepo = errorLogRepo;
        this.knowledgeRepo = knowledgeRepo;
    }

    /**
     * 上传文件并关联到错题或知识条目
     * @param file 上传的文件
     * @param type 类型 ("error" 或 "knowledge")
     * @param entityId 关联的实体 ID
     * @return 包含文件访问路径的响应
     */
    @PostMapping("/upload")
    @Transactional // 需要事务来更新实体
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type, // "error" or "knowledge"
            @RequestParam("entityId") Long entityId) {

        log.info("Received file upload request: type={}, entityId={}, filename={}", type, entityId, file.getOriginalFilename());

        String subDirectory;
        String fileIdentifier;

        // 根据类型确定子目录
        if ("error".equalsIgnoreCase(type)) {
            subDirectory = "errors";
        } else if ("knowledge".equalsIgnoreCase(type)) {
            subDirectory = "knowledge";
        } else {
            return ResponseEntity.badRequest().body("Invalid upload type.");
        }

        try {
            // 存储文件并获取标识符 (相对路径)
            fileIdentifier = fileStorageStrategy.storeFile(file, subDirectory);

            // 更新对应实体的文件字段
            boolean updated = false;
            if ("error".equalsIgnoreCase(type)) {
                updated = errorLogRepo.findById(entityId).map(entry -> {
                    // 如果之前有文件，先删除旧文件 (可选)
                    if (entry.getImageFile() != null) {
                        try { fileStorageStrategy.deleteFile(entry.getImageFile(), null); } // 假设旧标识符已包含子目录
                        catch (Exception e) { log.warn("Could not delete old error image file: {}", entry.getImageFile(), e); }
                    }
                    entry.setImageFile(fileIdentifier); // 保存新的文件标识符
                    errorLogRepo.save(entry);
                    return true;
                }).orElse(false);
            } else if ("knowledge".equalsIgnoreCase(type)) {
                updated = knowledgeRepo.findById(entityId).map(item -> {
                    if (item.getLinkedFile() != null) {
                        try { fileStorageStrategy.deleteFile(item.getLinkedFile(), null); }
                        catch (Exception e) { log.warn("Could not delete old knowledge linked file: {}", item.getLinkedFile(), e); }
                    }
                    item.setLinkedFile(fileIdentifier);
                    knowledgeRepo.save(item);
                    return true;
                }).orElse(false);
            }

            if (!updated) {
                log.warn("Entity not found for type {} and id {}, could not link uploaded file.", type, entityId);
                // 虽然文件上传了，但未关联成功，可以选择删除已上传的文件或返回特定错误
                try { fileStorageStrategy.deleteFile(fileIdentifier, null); }
                catch (Exception e) { log.error("Could not delete unlinked uploaded file: {}", fileIdentifier, e); }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Associated entity not found.");
            }

            // 构建文件的可访问 URI (相对于当前应用)
            // 注意：这只是一个相对路径，前端需要配合基础 URL 使用
            // 或者直接返回 fileIdentifier 让前端构造完整 URL
            // String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            //         .path("/api/files/download/")
            //         // .path(subDirectory + "/") // subDirectory 已经包含在 fileIdentifier 中
            //         .path(fileIdentifier)
            //         .toUriString();

            // 返回文件标识符给前端
            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully!",
                    "fileIdentifier", fileIdentifier // 返回路径/标识符
                    // "fileDownloadUri", fileDownloadUri // 可选返回完整下载 URI
            ));

        } catch (RuntimeException e) {
            log.error("Could not upload file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload file: " + e.getMessage());
        }
    }

    /**
     * 下载/访问文件
     * @param subDirectory 子目录 (路径的一部分)
     * @param filename 文件名 (路径的另一部分)
     * @return 文件资源
     */
    @GetMapping("/download/{subDirectory}/{filename:.+}") // :.+ 匹配包含点的文件名
    public ResponseEntity<Resource> downloadFile(@PathVariable String subDirectory, @PathVariable String filename) {
        // 从策略加载文件资源
        String fileIdentifier = subDirectory + "/" + filename;
        Resource resource = fileStorageStrategy.loadFileAsResource(fileIdentifier, null); // subDirectory 已在 identifier 中

        // 尝试确定文件的 Content-Type
        String contentType = null;
        try {
            contentType = Files.probeContentType(fileStorageStrategy.getFilePath(fileIdentifier, null)); // 获取文件路径
        } catch (IOException ex) {
            log.info("Could not determine file type for: {}", fileIdentifier);
        }
        // 如果无法确定，则使用默认二进制流类型
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                // 设置 Content-Disposition 让浏览器尝试显示或下载
                // attachment; 会强制下载， inline; 会尝试在浏览器中打开
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // --- 添加一个删除文件的端点 (如果需要前端直接删除文件) ---
    // DELETE /api/files/{subDirectory}/{filename:.+}
    // @DeleteMapping("/{subDirectory}/{filename:.+}")
    // public ResponseEntity<?> deleteFileEndpoint(@PathVariable String subDirectory, @PathVariable String filename) {
    //     try {
    //         String fileIdentifier = subDirectory + "/" + filename;
    //         fileStorageStrategy.deleteFile(fileIdentifier, null);
    //         // 注意：这里只删除了物理文件，数据库中对应的实体字段需要另外处理（通常在删除实体时处理）
    //         return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
    //     } catch (RuntimeException e) {
    //          log.error("Error deleting file: {}", filename, e);
    //          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not delete file.");
    //     }
    // }

}