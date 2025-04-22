package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.ErrorLogEntry;
import com.example.gwy_backend.repository.ErrorLogEntryRepository;
import com.example.gwy_backend.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant; // <<< 导入 Instant
// import java.time.LocalDateTime; // 不再需要导入 LocalDateTime
import java.util.List;
import java.util.Optional;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogEntryRepository errorLogEntryRepository;

    @Autowired
    public ErrorLogServiceImpl(ErrorLogEntryRepository errorLogEntryRepository) {
        this.errorLogEntryRepository = errorLogEntryRepository;
    }

    // --- 以下方法无需修改时间逻辑，因为它们处理的是已包含 Instant 的实体 ---

    @Override
    @Transactional(readOnly = true)
    public List<ErrorLogEntry> getAllErrorLogsSorted() {
        // Repository 方法会基于 Instant 类型的 timestamp 字段进行排序
        return errorLogEntryRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorLogEntry> getErrorLogsBySubject(String subject) {
        // 假设 Repository 方法不需要特殊的时间处理
        // 如果需要按模块查询也排序：
        // return errorLogEntryRepository.findBySubjectIgnoreCaseOrderByTimestampDesc(subject);
        return errorLogEntryRepository.findBySubjectIgnoreCase(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ErrorLogEntry> getErrorLogById(Long id) {
        return errorLogEntryRepository.findById(id);
    }

    @Override
    @Transactional
    public ErrorLogEntry addErrorLog(ErrorLogEntry errorLogEntry) {
        // ID 由数据库生成
        errorLogEntry.setId(null);
        // timestamp 会通过 @PrePersist 自动设置 (现在是 Instant)
        // reviewCount 默认为 0
        // lastReviewDate 初始应为 null
        errorLogEntry.setLastReviewDate(null); // 确保添加时为 null
        return errorLogEntryRepository.save(errorLogEntry);
    }

    // --- markAsReviewed 方法修改 ---
    @Override
    @Transactional
    public Optional<ErrorLogEntry> markAsReviewed(Long id) {
        // 查找实体
        return errorLogEntryRepository.findById(id).map(entry -> {
            // 增加复习次数
            entry.setReviewCount(entry.getReviewCount() + 1);
            // 更新上次复习时间为当前的 UTC 时间点
            entry.setLastReviewDate(Instant.now()); // <<< 使用 Instant.now()
            // 保存更新后的实体
            return errorLogEntryRepository.save(entry);
        });
    }

    // --- deleteErrorLog 方法无需修改时间逻辑 ---
    @Override
    @Transactional
    public boolean deleteErrorLog(Long id) {
        // 检查是否存在
        if (errorLogEntryRepository.existsById(id)) {
            // 删除（关联文件应由更复杂的逻辑处理，比如在 Service 中调用文件服务）
            errorLogEntryRepository.deleteById(id);
            return true; // 删除成功
        }
        return false; // 记录不存在
    }
}