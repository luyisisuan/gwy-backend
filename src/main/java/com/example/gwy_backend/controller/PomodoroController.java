package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.PomodoroSettings;
import com.example.gwy_backend.entity.StudyLog;
import com.example.gwy_backend.service.PomodoroService; // 确保 PomodoroService 有 clearAllLogs 方法
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pomodoro") // 基础路径 /api/pomodoro
public class PomodoroController {

    private final PomodoroService pomodoroService;
    private static final String DEFAULT_SETTINGS_KEY = "default_user"; // 默认设置 key

    @Autowired
    public PomodoroController(PomodoroService pomodoroService) {
        this.pomodoroService = pomodoroService;
    }

    // --- Settings Endpoints ---

    // GET /api/pomodoro/settings - 获取番茄钟设置
    @GetMapping("/settings")
    public ResponseEntity<PomodoroSettings> getSettings() {
        PomodoroSettings settings = pomodoroService.getSettings(DEFAULT_SETTINGS_KEY);
        return ResponseEntity.ok(settings);
    }

    // PATCH /api/pomodoro/settings - 更新番茄钟设置
    @PatchMapping("/settings")
    public ResponseEntity<PomodoroSettings> updateSettings(@RequestBody Map<String, Integer> updates) {
        return pomodoroService.updateSettings(DEFAULT_SETTINGS_KEY, updates)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 理论上不会发生
    }

    // --- Study Log Endpoints ---

    // POST /api/pomodoro/log - 添加学习日志记录
    @PostMapping("/log")
    public ResponseEntity<StudyLog> addStudyLog(@RequestBody StudyLog studyLog) {
        // 可以在这里添加对 studyLog 的基本验证
        if (studyLog.getStartTime() == null || studyLog.getEndTime() == null ||
                studyLog.getStartTime().isAfter(studyLog.getEndTime()) || studyLog.getDurationSeconds() <= 0) {
            return ResponseEntity.badRequest().build(); // 简单验证
        }
        StudyLog createdLog = pomodoroService.addStudyLog(studyLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
    }

    // GET /api/pomodoro/log/recent - 获取最近的学习日志
    @GetMapping("/log/recent")
    public ResponseEntity<List<StudyLog>> getRecentLogs(
            @RequestParam(defaultValue = "50") int limit // 接收可选的 limit 参数，默认为 50
    ) {
        if (limit <= 0) limit = 50; // 简单验证
        List<StudyLog> logs = pomodoroService.getRecentStudyLogs(limit);
        return ResponseEntity.ok(logs);
    }

    // DELETE /api/pomodoro/log/all - 清空所有学习日志  <<< 添加这个方法
    @DeleteMapping("/log/all")
    public ResponseEntity<Void> clearAllStudyLogs() {
        pomodoroService.clearAllLogs(); // 调用 Service 层的方法
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }


    // --- 未来可以添加获取统计信息的端点 ---
    // 例如 GET /api/pomodoro/stats?period=today / week / month
    // 后端计算并返回总时长
}