package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.PomodoroSettings;
import com.example.gwy_backend.entity.StudyLog;
import com.example.gwy_backend.event.StudyLogAddedEvent; // <<< 导入事件类
import com.example.gwy_backend.repository.PomodoroSettingsRepository;
import com.example.gwy_backend.repository.StudyLogRepository;
import com.example.gwy_backend.service.PomodoroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher; // <<< 导入事件发布器
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PomodoroServiceImpl implements PomodoroService {

    private static final Logger log = LoggerFactory.getLogger(PomodoroServiceImpl.class);
    private final PomodoroSettingsRepository settingsRepository;
    private final StudyLogRepository studyLogRepository;
    private final ApplicationEventPublisher eventPublisher; // <<< 注入事件发布器

    @Autowired
    public PomodoroServiceImpl(PomodoroSettingsRepository settingsRepository,
                               StudyLogRepository studyLogRepository,
                               ApplicationEventPublisher eventPublisher) { // <<< 修改构造函数
        this.settingsRepository = settingsRepository;
        this.studyLogRepository = studyLogRepository;
        this.eventPublisher = eventPublisher; // <<< 注入
    }

    // --- Settings Implementation ---

    @Override
    @Transactional // 可能创建默认值，需要事务
    public PomodoroSettings getSettings(String settingsKey) {
        log.info("Fetching settings for key: {}", settingsKey);
        return settingsRepository.findBySettingsKey(settingsKey)
                .orElseGet(() -> {
                    log.info("Settings not found for key: {}, creating default.", settingsKey);
                    PomodoroSettings defaultSettings = new PomodoroSettings();
                    defaultSettings.setSettingsKey(settingsKey);
                    // 使用实体类中定义的默认值 (25, 5, 15)
                    return settingsRepository.save(defaultSettings);
                });
    }

    @Override
    @Transactional // 更新操作，需要事务
    public Optional<PomodoroSettings> updateSettings(String settingsKey, Map<String, Integer> updates) {
        log.info("Attempting to update settings for key: {} with updates: {}", settingsKey, updates);
        Optional<PomodoroSettings> existingSettingsOptional = settingsRepository.findBySettingsKey(settingsKey);

        if (existingSettingsOptional.isPresent()) {
            PomodoroSettings settingsToUpdate = existingSettingsOptional.get();
            boolean changed = false;

            if (updates.containsKey("workDuration")) {
                int value = Math.max(1, updates.getOrDefault("workDuration", settingsToUpdate.getWorkDuration()));
                if (settingsToUpdate.getWorkDuration() != value) {
                    settingsToUpdate.setWorkDuration(value);
                    changed = true;
                    log.debug("Updating workDuration to: {}", value);
                }
            }
            if (updates.containsKey("shortBreakDuration")) {
                int value = Math.max(1, updates.getOrDefault("shortBreakDuration", settingsToUpdate.getShortBreakDuration()));
                if (settingsToUpdate.getShortBreakDuration() != value) {
                    settingsToUpdate.setShortBreakDuration(value);
                    changed = true;
                    log.debug("Updating shortBreakDuration to: {}", value);
                }
            }
            if (updates.containsKey("longBreakDuration")) {
                int value = Math.max(1, updates.getOrDefault("longBreakDuration", settingsToUpdate.getLongBreakDuration()));
                if (settingsToUpdate.getLongBreakDuration() != value) {
                    settingsToUpdate.setLongBreakDuration(value);
                    changed = true;
                    log.debug("Updating longBreakDuration to: {}", value);
                }
            }

            if (changed) {
                log.info("Saving updated settings for key: {}", settingsKey);
                return Optional.of(settingsRepository.save(settingsToUpdate));
            } else {
                log.info("No changes detected for settings key: {}, skipping save.", settingsKey);
                return Optional.of(settingsToUpdate); // 返回未改变的对象
            }
        } else {
            log.warn("Settings not found for key: {}, update failed.", settingsKey);
            return Optional.empty();
        }
    }

    // --- Study Log Implementation ---
    @Override
    @Transactional
    public StudyLog addStudyLog(StudyLog studyLog) {
        log.info("Adding new study log: Activity - '{}', Duration - {}s", studyLog.getActivity(), studyLog.getDurationSeconds());
        studyLog.setId(null); // 确保是新增
        StudyLog savedLog = studyLogRepository.save(studyLog); // 保存

        // **MODIFIED:** 发布事件
        if (savedLog != null && savedLog.getId() != null) { // 确保保存成功且有 ID
            log.debug("Publishing StudyLogAddedEvent for log id: {}", savedLog.getId());
            try {
                eventPublisher.publishEvent(new StudyLogAddedEvent(this, savedLog)); // <<< 发布事件
            } catch (Exception e) {
                // 记录发布事件时可能发生的异常，但不应中断主流程
                log.error("Error publishing StudyLogAddedEvent for log id: {}", savedLog.getId(), e);
            }
        } else {
            log.warn("Study log was not saved correctly, event not published.");
        }

        return savedLog;
    }
    @Override
    @Transactional(readOnly = true) // 只读操作
    public List<StudyLog> getRecentStudyLogs(int limit) {
        log.info("Fetching recent {} study logs.", limit);
        // 使用分页获取最新记录，按 startTime 降序
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        // getContent() 返回当前页的 List<StudyLog>
        return studyLogRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional // 清空是修改操作，需要事务
    public void clearAllLogs() {
        log.warn("Clearing all study logs!"); // 使用 warn 级别日志记录此操作
        // 调用 Repository 中的自定义方法或 JPA 提供的 deleteAll
        studyLogRepository.deleteAllLogs(); // 使用我们定义的 JPQL 方法
        // 或者 studyLogRepository.deleteAll(); // JPA 标准方法，效果相同但可能效率稍低
        log.info("All study logs have been cleared.");
    }
}