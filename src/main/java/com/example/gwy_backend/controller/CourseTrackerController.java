package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.CourseTracker;
import com.example.gwy_backend.service.CourseTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course-tracker") // 基础路径
public class CourseTrackerController {

    private final CourseTrackerService courseTrackerService;
    private static final String DEFAULT_COURSE_KEY = "main-course"; // 定义默认 key

    @Autowired
    public CourseTrackerController(CourseTrackerService courseTrackerService) {
        this.courseTrackerService = courseTrackerService;
    }

    // GET /api/course-tracker - 获取主课程的追踪信息
    @GetMapping
    public ResponseEntity<CourseTracker> getMainCourseTracker() {
        CourseTracker tracker = courseTrackerService.getCourseTracker(DEFAULT_COURSE_KEY);
        return ResponseEntity.ok(tracker);
    }

    // PATCH /api/course-tracker - 更新主课程的追踪信息
    // 使用 PATCH 更符合部分更新的语义
    @PatchMapping
    public ResponseEntity<CourseTracker> updateMainCourseTracker(@RequestBody Map<String, Object> updates) {
        return courseTrackerService.updateCourseTracker(DEFAULT_COURSE_KEY, updates)
                .map(ResponseEntity::ok) // 成功则返回 200 OK 和更新后的数据
                .orElse(ResponseEntity.notFound().build()); // 理论上不会发生，因为 get 会创建
    }

    // 如果将来需要支持多个课程，可以添加类似 GET /api/course-tracker/{courseKey} 的端点
}