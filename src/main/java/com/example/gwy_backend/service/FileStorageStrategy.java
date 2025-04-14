package com.example.gwy_backend.service; // 或 service.storage

import org.springframework.core.io.Resource; // 用于返回文件资源
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageStrategy {

    /**
     * 存储上传的文件。
     * @param file 上传的文件对象
     * @param subDirectory 可选的子目录 (例如 "errors", "knowledge")
     * @return 存储后用于访问文件的唯一标识符或相对路径
     * @throws RuntimeException 如果存储失败
     */
    String storeFile(MultipartFile file, String subDirectory) throws RuntimeException;

    /**
     * 根据文件标识符加载文件资源。
     * @param fileIdentifier storeFile 方法返回的标识符
     * @param subDirectory 可选的子目录
     * @return 文件资源对象
     * @throws RuntimeException 如果文件找不到或无法读取
     */
    Resource loadFileAsResource(String fileIdentifier, String subDirectory) throws RuntimeException;

    /**
     * 根据文件标识符删除文件。
     * @param fileIdentifier storeFile 方法返回的标识符
     * @param subDirectory 可选的子目录
     * @throws RuntimeException 如果删除失败
     */
    void deleteFile(String fileIdentifier, String subDirectory) throws RuntimeException;

    /**
     * 获取文件的完整路径 (如果适用，例如本地存储)
     * @param fileIdentifier storeFile 方法返回的标识符
     * @param subDirectory 可选的子目录
     * @return 文件的 Path 对象
     */
    Path getFilePath(String fileIdentifier, String subDirectory); // 可选方法
}