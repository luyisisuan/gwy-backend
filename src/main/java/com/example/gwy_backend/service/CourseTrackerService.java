package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.CourseTracker;
import java.util.Optional;

// DTO (Data Transfer Object) 用于更新，避免直接暴露 Entity
// 可以单独创建 DTO 类，或者简单使用 Map
import java.util.Map;

public interface CourseTrackerService {

    // 获取指定 key 的课程追踪信息，如果不存在则创建默认的
    CourseTracker getCourseTracker(String courseKey);

    // 更新指定 key 的课程追踪信息
    // 使用 Map<String, Object> 作为 DTO，只更新传入的字段
    Optional<CourseTracker> updateCourseTracker(String courseKey, Map<String, Object> updates);
}