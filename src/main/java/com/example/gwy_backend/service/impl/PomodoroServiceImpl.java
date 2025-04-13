package com.example.gwy_backend.service.impl; // <<< 确认包名

import com.example.gwy_backend.entity.PomodoroSettings;    // <<< 确认 Entity 路径
import com.example.gwy_backend.entity.StudyLog;          // <<< 确认 Entity 路径
import com.example.gwy_backend.repository.PomodoroSettingsRepository; // <<< 确认 Repository 路径
import com.example.gwy_backend.repository.StudyLogRepository;       // <<< 确认 Repository 路径
import com.example.gwy_backend.service.PomodoroService;         // <<< 确认 Service 接口路径
import org.slf4j.Logger; // 引入日志
import org.slf4j.LoggerFactory; // 引入日志
import org.springframework.beans.factory.annotation.Autowired;
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

    // 添加日志记录器
    private static final Logger log = LoggerFactory.getLogger(PomodoroServiceImpl.class);

    private final PomodoroSettingsRepository settingsRepository;
    private final StudyLogRepository studyLogRepository;

    @Autowired
    public PomodoroServiceImpl(PomodoroSettingsRepository settingsRepository, StudyLogRepository studyLogRepository) {
        this.settingsRepository = settingsRepository;
        this.studyLogRepository = studyLogRepository;
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
    @Transactional // 添加操作，需要事务
    public StudyLog addStudyLog(StudyLog studyLog) {
        log.info("Adding new study log: Activity - {}", studyLog.getActivity());
        studyLog.setId(null); // 确保 ID 为 null 以进行插入
        // 可以在此添加更多服务器端验证
        return studyLogRepository.save(studyLog);
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