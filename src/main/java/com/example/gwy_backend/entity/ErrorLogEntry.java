package com.example.gwy_backend.entity;

import jakarta.persistence.*; // 使用 jakarta for Spring Boot 3+
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant; // <<< 导入 Instant，替换 LocalDateTime

@Entity
@Data // Lombok: 自动生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 生成无参构造函数
@AllArgsConstructor // Lombok: 生成全参构造函数
@Table(name = "error_log_entry") // 建议明确指定表名
public class ErrorLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 数据库自增 ID
    private Long id;

    // --- 时间字段修改为 Instant ---
    @Column(nullable = false, updatable = false) // 创建时间不能为空且创建后不可更新
    private Instant timestamp; // 记录时间 (使用 Instant 表示 UTC 时间点)

    @Column
    private Instant lastReviewDate; // 上次复习时间 (使用 Instant，可以为 null)
    // --- 其他字段保持不变 ---

    @Column(columnDefinition = "TEXT", nullable = false) // 题干不能为空
    private String question; // 题干/问题描述

    @Column(nullable = false) // 模块不能为空
    private String subject; // 所属模块

    private String myAnswer; // 我的答案 (可以为空)

    @Column(nullable = false) // 正确答案不能为空
    private String correctAnswer; // 正确答案

    private String knowledgePoint; // 关联知识点 (可以为空)

    @Column(columnDefinition = "TEXT", nullable = false) // 原因不能为空
    private String reason; // 错误原因分析

    private String imageFile; // 截图文件标识符 (可以为空)

    @Column(nullable = false) // 复习次数不能为空
    private int reviewCount = 0; // 复习次数，默认为 0

    // --- Lifecycle Callback 修改 ---
    @PrePersist // 在持久化 (INSERT) 之前执行
    protected void onCreate() {
        // 自动设置当前 UTC 时间为创建时间
        if (this.timestamp == null) { // 确保只在创建时设置一次
            this.timestamp = Instant.now(); // <<< 使用 Instant.now() 获取 UTC 时间
        }
        // 可以在此强制 reviewCount 为 0，虽然默认值已设置
        this.reviewCount = 0;
    }

    // 注意：Lombok 的 @Data 会自动生成所需的方法，无需手动编写 Getters/Setters

    // @PreUpdate 不再需要，更新 lastReviewDate 的逻辑放在 Service 层更清晰
}