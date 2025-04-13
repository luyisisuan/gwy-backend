package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.ErrorLogEntry;
import com.example.gwy_backend.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/errors") // 基础路径设置为 /api/errors
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @Autowired
    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    // GET /api/errors - 获取所有错题记录 (按时间倒序)
    // 或 GET /api/errors?subject=xxx - 按模块筛选
    @GetMapping
    public ResponseEntity<List<ErrorLogEntry>> getErrorLogs(
            @RequestParam(required = false) String subject) { // @RequestParam 用于接收查询参数 ?subject=xxx
        List<ErrorLogEntry> errors;
        if (subject != null && !subject.trim().isEmpty()) {
            // 如果提供了 subject 参数，则按模块查询
            errors = errorLogService.getErrorLogsBySubject(subject);
        } else {
            // 否则，获取所有记录（已排序）
            errors = errorLogService.getAllErrorLogsSorted();
        }
        return ResponseEntity.ok(errors);
    }

    // GET /api/errors/{id} - 获取单个错题记录
    @GetMapping("/{id}")
    public ResponseEntity<ErrorLogEntry> getErrorLogById(@PathVariable Long id) {
        return errorLogService.getErrorLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/errors - 添加新的错题记录
    @PostMapping
    public ResponseEntity<ErrorLogEntry> addErrorLog(@RequestBody ErrorLogEntry errorLogEntry) {
        ErrorLogEntry createdEntry = errorLogService.addErrorLog(errorLogEntry);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
    }

    // PATCH /api/errors/{id}/review - 标记为已复习
    @PatchMapping("/{id}/review")
    public ResponseEntity<ErrorLogEntry> markAsReviewed(@PathVariable Long id) {
        return errorLogService.markAsReviewed(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/errors/{id} - 删除错题记录
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteErrorLog(@PathVariable Long id) {
        if (errorLogService.deleteErrorLog(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}