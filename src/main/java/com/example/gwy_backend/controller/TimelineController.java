package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.TimelineTask;
import com.example.gwy_backend.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct; // 用于初始化
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timeline") // 基础路径 /api/timeline
public class TimelineController {

    private final TimelineService timelineService;

    @Autowired
    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    // --- 初始化数据 ---
    // 应用启动后自动尝试初始化任务列表 (仅当数据库为空时)
    @PostConstruct
    public void initTasks() {
        // 定义初始任务列表 (硬编码或从配置文件读取)
        List<TimelineTask> initialTasks = List.of(
                // Phase 1
                new TimelineTask("task-phase1-1-timeline", "系统学习行测五大模块基础知识与方法论", "phase1", false),
                new TimelineTask("task-phase1-2-timeline", "掌握申论基本题型作答思路", "phase1", false),
                new TimelineTask("task-phase1-3-timeline", "开始分模块专项练习（注重理解）", "phase1", false),
                new TimelineTask("task-phase1-4-timeline", "持续关注时事政治（学习强国/官媒）", "phase1", false),
                new TimelineTask("task-phase1-5-timeline", "加强中共党史、理论学习", "phase1", false),
                new TimelineTask("task-phase1-6-timeline", "初步研究往年职位表，思考方向", "phase1", false),
                // Phase 2
                new TimelineTask("task-phase2-1-timeline", "高强度、系统性刷题训练 (行测+申论)", "phase2", false),
                new TimelineTask("task-phase2-2-timeline", "进行套题模拟，严格控时", "phase2", false),
                new TimelineTask("task-phase2-3-timeline", "重点攻克弱项模块/题型，分析错题", "phase2", false),
                new TimelineTask("task-phase2-4-timeline", "加强申论写作练习 (大作文+应用文)", "phase2", false),
                new TimelineTask("task-phase2-5-timeline", "整理常识体系和申论素材库", "phase2", false),
                // Phase 3
                new TimelineTask("task-phase3-1-timeline", "以历年真题和高质量模拟题进行全真模考", "phase3", false),
                new TimelineTask("task-phase3-2-timeline", "查漏补缺，回归基础，巩固高频考点", "phase3", false),
                new TimelineTask("task-phase3-3-timeline", "强化记忆常识关键信息和申论热点", "phase3", false),
                new TimelineTask("task-phase3-4-timeline", "调整作息，模拟考试时间，保持心态", "phase3", false),
                new TimelineTask("task-phase3-5-timeline", "关注最终公告和职位表，确认报考岗位", "phase3", false)
        );
        timelineService.initializeTasks(initialTasks);
    }

    // --- API Endpoints ---

    // GET /api/timeline/tasks - 获取所有任务 (平铺列表形式)
    @GetMapping("/tasks")
    public ResponseEntity<List<TimelineTask>> getAllTasks() {
        List<TimelineTask> tasks = timelineService.getAllTasksList();
        return ResponseEntity.ok(tasks);
    }

    // GET /api/timeline/tasks/grouped - 获取所有任务 (按阶段分组形式)
    @GetMapping("/tasks/grouped")
    public ResponseEntity<Map<String, List<TimelineTask>>> getAllTasksGrouped() {
        Map<String, List<TimelineTask>> groupedTasks = timelineService.getAllTasksGroupedByPhase();
        return ResponseEntity.ok(groupedTasks);
    }

    // PATCH /api/timeline/tasks/{taskId} - 更新单个任务的完成状态
    // 请求体只需要包含 "completed": true/false
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<Void> updateTaskCompletion(
            @PathVariable String taskId,
            @RequestBody Map<String, Boolean> payload) {

        if (!payload.containsKey("completed")) {
            return ResponseEntity.badRequest().build(); // 请求体格式错误
        }
        boolean completed = payload.get("completed");
        boolean success = timelineService.updateTaskCompletion(taskId, completed);

        if (success) {
            return ResponseEntity.ok().build(); // 返回 200 OK (或 204 No Content)
        } else {
            // 可能是 taskId 不存在
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
    }

    // 注意：Timeline 笔记的 API 使用 NoteController 中的 PUT /api/notes/{noteKey}
    // 前端需要使用 "notes-phase1", "notes-phase2", "notes-phase3" 作为 noteKey
}