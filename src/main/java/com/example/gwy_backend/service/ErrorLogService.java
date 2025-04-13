package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.ErrorLogEntry;
import java.util.List;
import java.util.Optional;

public interface ErrorLogService {

    List<ErrorLogEntry> getAllErrorLogsSorted(); // 获取所有错题（按时间倒序）

    List<ErrorLogEntry> getErrorLogsBySubject(String subject); // 按模块获取错题

    Optional<ErrorLogEntry> getErrorLogById(Long id); // 按 ID 获取错题

    ErrorLogEntry addErrorLog(ErrorLogEntry errorLogEntry); // 添加错题

    Optional<ErrorLogEntry> markAsReviewed(Long id); // 标记为已复习

    boolean deleteErrorLog(Long id); // 删除错题
}