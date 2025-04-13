package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.ErrorLogEntry;
import com.example.gwy_backend.repository.ErrorLogEntryRepository;
import com.example.gwy_backend.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogEntryRepository errorLogEntryRepository;

    @Autowired
    public ErrorLogServiceImpl(ErrorLogEntryRepository errorLogEntryRepository) {
        this.errorLogEntryRepository = errorLogEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorLogEntry> getAllErrorLogsSorted() {
        // 使用 Repository 中定义的方法按时间戳倒序获取
        return errorLogEntryRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ErrorLogEntry> getErrorLogsBySubject(String subject) {
        // 使用 Repository 中定义的方法按模块查询
        // 这里也按时间倒序返回可能更好，可以在 Repository 方法名添加 OrderBy 或使用 @Query
        // 为了简单，先直接返回
        return errorLogEntryRepository.findBySubjectIgnoreCase(subject);
        // 如果需要排序:
        // return errorLogEntryRepository.findBySubjectIgnoreCaseOrderByTimestampDesc(subject); // 需要在 Repository 定义此方法
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ErrorLogEntry> getErrorLogById(Long id) {
        return errorLogEntryRepository.findById(id);
    }

    @Override
    @Transactional
    public ErrorLogEntry addErrorLog(ErrorLogEntry errorLogEntry) {
        // 清除 ID，让数据库生成
        errorLogEntry.setId(null);
        // timestamp 会通过 @PrePersist 自动设置
        // reviewCount 默认为 0
        // lastReviewDate 初始为 null
        return errorLogEntryRepository.save(errorLogEntry);
    }

    @Override
    @Transactional
    public Optional<ErrorLogEntry> markAsReviewed(Long id) {
        return errorLogEntryRepository.findById(id).map(entry -> {
            // 增加复习次数
            entry.setReviewCount(entry.getReviewCount() + 1);
            // 更新上次复习时间为当前时间
            entry.setLastReviewDate(LocalDateTime.now());
            // 保存更新
            return errorLogEntryRepository.save(entry);
        });
    }

    @Override
    @Transactional
    public boolean deleteErrorLog(Long id) {
        if (errorLogEntryRepository.existsById(id)) {
            errorLogEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}