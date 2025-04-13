package com.example.gwy_backend.service; // <<< 确认包名

import com.example.gwy_backend.entity.PomodoroSettings; // <<< 确认 Entity 路径
import com.example.gwy_backend.entity.StudyLog;       // <<< 确认 Entity 路径
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PomodoroService {

    // --- Settings ---
    /**
     * 获取指定 key 的番茄钟设置。如果不存在，则创建并返回默认设置。
     * @param settingsKey 设置的唯一标识符
     * @return PomodoroSettings 对象
     */
    PomodoroSettings getSettings(String settingsKey);

    /**
     * 更新指定 key 的番茄钟设置。只更新 Map 中存在的字段。
     * @param settingsKey 设置的唯一标识符
     * @param updates 包含要更新的设置字段和值的 Map (e.g., {"workDuration": 30})
     * @return 包含更新后设置的 Optional，如果 key 不存在则为空 Optional
     */
    Optional<PomodoroSettings> updateSettings(String settingsKey, Map<String, Integer> updates);

    // --- Study Log ---
    /**
     * 添加一条新的学习日志记录。
     * @param studyLog 要添加的 StudyLog 对象 (ID 应为 null)
     * @return 保存后的 StudyLog 对象 (包含数据库生成的 ID)
     */
    StudyLog addStudyLog(StudyLog studyLog);

    /**
     * 获取最近指定数量的学习日志记录，按开始时间倒序排列。
     * @param limit 要获取的记录数量
     * @return StudyLog 列表
     */
    List<StudyLog> getRecentStudyLogs(int limit);

    /**
     * 清空所有的学习日志记录。
     */
    void clearAllLogs(); // <<< 添加清空日志的方法声明
}