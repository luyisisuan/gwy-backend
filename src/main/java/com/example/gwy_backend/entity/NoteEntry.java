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
// 移除 noteKey 的唯一索引，可以保留普通索引或 lastUpdated 索引
@Table(name = "note_entry",
        indexes = { @Index(name = "idx_note_timestamp", columnList = "timestamp DESC") }) // 按时间戳降序索引
public class NoteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // noteKey 不再唯一，仅作为分类或来源标识 (可以为 null)
    @Column(length = 100)
    private String noteKey;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false) // 内容不能为空
    private String content; // 笔记内容

    @Column(nullable = false) // 创建时间戳不为空
    private LocalDateTime timestamp; // 创建时间

    // 移除 lastUpdated 和 @PreUpdate，因为日志式通常不更新
    // private LocalDateTime lastUpdated;

    @PrePersist // 只在创建时设置时间戳
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    // @PreUpdate
    // protected void onUpdate() { ... } // 移除
}