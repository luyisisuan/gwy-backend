package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List; // 用于存储标签列表

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp; // 添加时间

    @Column(nullable = false)
    private String title; // 标题 (不能为空)

    @Column(nullable = false)
    private String category; // 分类 (不能为空)

    @Lob // 这很好，确保 content 可以是长文本
    @Column(columnDefinition = "TEXT", nullable = false) // 明确指定 TEXT 类型且不能为空
    private String content; // 内容 (不能为空)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "knowledge_item_tags", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "tag")
    private List<String> tags; // 标签列表

    // --- 修改这里 ---
    @Column(length = 2048) // 指定长度以匹配数据库中的 VARCHAR(2048)
    private String externalLink; // 外部链接 (可以为 null)
    // --- 结束修改 ---

    private String linkedFile; // 关联文件名 (可以为 null)

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now(); // 在持久化之前设置当前时间
    }
}