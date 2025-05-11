package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// 添加唯一约束和索引
@Table(indexes = { @Index(name = "idx_course_key", columnList = "courseKey", unique = true) })
public class CourseTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String courseKey = "main-course"; // 唯一标识符, 默认 "main-course"

    private String courseName = "100元红鲶定金班 (华图)"; // 课程名称 (可以默认或允许修改)
    private String courseLink = "https://www.huatu.com/htzx/user/index.shtml#/courseDetail/KCZF464FO6/100%E5%85%83%E7%BA%A2%E9%B2%A4%E5%AE%9A%E9%87%91%E7%8F%AD?orderCode="; // 课程链接

    private int totalLessons = 1; // 总课时 (默认值)
    private int completedLessons = 0; // 已完成课时 (默认值)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes; // 课程笔记

    private LocalDateTime lastUpdated; // 最后更新时间

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}