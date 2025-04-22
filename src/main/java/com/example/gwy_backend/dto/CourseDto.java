package com.example.gwy_backend.dto;

import com.example.gwy_backend.entity.Course.CourseStatus; // 导入状态枚举
import lombok.Data;
import java.time.Instant;

@Data // Lombok: 自动生成 getter, setter 等
public class CourseDto {
    private Long id;
    private String name;
    private String link;
    private int totalLessons;
    private int completedLessons;
    private CourseStatus status; // <<< 使用枚举类型
    private String category;
    private Instant createdAt;
    private Instant lastUpdated;
    private int progressPercentage; // <<< 添加计算好的进度百分比
}