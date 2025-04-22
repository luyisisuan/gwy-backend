package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant; // <<< 导入 Instant，替换 LocalDateTime

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "note_entry",
        indexes = { @Index(name = "idx_note_timestamp", columnList = "timestamp DESC") }) // 按时间戳降序索引
public class NoteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 时间字段修改为 Instant ---
    @Column(nullable = false, updatable = false) // 创建时间戳不为空且不可更新
    private Instant timestamp; // 创建时间 (使用 Instant 表示 UTC 时间点)
    // --- 其他字段 ---

    @Column(length = 100) // 来源标识，可以为空
    private String noteKey;

    @Lob // 表示可能的大对象，映射到数据库的 TEXT 或 CLOB 类型
    @Column(columnDefinition = "TEXT", nullable = false) // 内容不能为空
    private String content; // 笔记内容

    // --- Lifecycle Callback 修改 ---
    @PrePersist // 只在创建时设置时间戳
    protected void onCreate() {
        // 自动设置当前 UTC 时间为创建时间
        if (this.timestamp == null) { // 确保只在创建时设置一次
            this.timestamp = Instant.now(); // <<< 使用 Instant.now() 获取 UTC 时间
        }
    }

    // 不再需要 lastUpdated 字段和 @PreUpdate
}