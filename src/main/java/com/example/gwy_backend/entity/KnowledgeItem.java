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

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 内容 (不能为空)

    // 使用 @ElementCollection 存储简单的字符串列表 (标签)
    // 这会在数据库中创建一张额外的表来存储标签和 KnowledgeItem 的关联
    @ElementCollection(fetch = FetchType.EAGER) // EAGER 表示加载 KnowledgeItem 时同时加载标签
    @CollectionTable(name = "knowledge_item_tags", joinColumns = @JoinColumn(name = "item_id")) // 指定关联表的名称和外键列名
    @Column(name = "tag") // 指定存储标签值的列名
    private List<String> tags; // 标签列表

    private String externalLink; // 外部链接 (可以为 null)

    private String linkedFile; // 关联文件名 (可以为 null)

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}