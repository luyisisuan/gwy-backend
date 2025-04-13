package com.example.gwy_backend.entity;

import jakarta.persistence.*; // 使用 jakarta for Spring Boot 3+
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // 使用 Java 8+ 的日期时间 API

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogEntry {

    @Id
    // 不再使用客户端生成的ID，让数据库生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp; // 记录时间

    @Column(columnDefinition = "TEXT") // 对于可能较长的文本，使用 TEXT 类型
    private String question; // 题干/问题描述

    private String subject; // 所属模块

    private String myAnswer; // 我的答案

    private String correctAnswer; // 正确答案

    private String knowledgePoint; // 关联知识点

    @Column(columnDefinition = "TEXT")
    private String reason; // 错误原因分析

    private String imageFile; // 截图文件名 (仅保存文件名)

    private int reviewCount = 0; // 复习次数，默认为 0

    private LocalDateTime lastReviewDate; // 上次复习时间 (可以为 null)

    // 在添加新条目时自动设置当前时间
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // 在更新条目时自动更新上次复习时间 (如果 reviewCount 增加了)
    // 注意：更复杂的逻辑可能需要放在 Service 层处理
    // @PreUpdate
    // protected void onUpdate() {
    //    // 这里可能需要比较旧的 reviewCount，逻辑较复杂，暂时不在实体类处理
    // }
}