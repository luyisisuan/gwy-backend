package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = { @Index(name = "idx_settings_key", columnList = "settingsKey", unique = true) })
public class PomodoroSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String settingsKey = "default_user"; // 唯一标识符，代表默认用户的设置

    private int workDuration = 25;    // 工作时长 (分钟)
    private int shortBreakDuration = 5; // 短休时长 (分钟)
    private int longBreakDuration = 15;  // 长休时长 (分钟)
    // longBreakInterval (长休间隔) 通常是前端逻辑，不一定需要存后端

}