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
public class StudyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime; // 开始时间

    @Column(nullable = false)
    private LocalDateTime endTime;   // 结束时间

    @Column(nullable = false)
    private long durationSeconds; // 持续时长 (秒)

    @Column(nullable = false)
    private String activity = "专注学习"; // 活动内容 (默认值)

    private String source = "pomodoro"; // 来源 (例如 'pomodoro', 'manual')

    // 可以考虑添加用户关联字段 (如果实现用户系统)
    // private Long userId;
}