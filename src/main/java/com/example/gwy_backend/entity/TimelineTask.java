package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// 添加索引，特别是如果经常按 phase 查询
@Table(indexes = { @Index(name = "idx_task_phase", columnList = "phase") })
public class TimelineTask {

    @Id
    // 注意：这次我们可能使用前端定义的 ID (例如 "task-phase1-1-timeline") 作为主键，
    // 因为任务列表是相对固定的。或者仍然让数据库生成 Long ID，并添加一个唯一的 taskKey 字段。
    // 为了简单起见，我们先尝试用前端 ID 作为主键 (需要确保它们在前端是唯一的)。
    // 如果前端 ID 可能变化或不适合做主键，则应改回自增 Long ID + taskKey 字段。
    private String id; // 使用前端定义的唯一 ID 作为主键

    @Column(nullable = false)
    private String label; // 任务描述

    @Column(nullable = false)
    private String phase; // 所属阶段标识 (e.g., "phase1", "phase2", "phase3")

    private boolean completed = false; // 完成状态

    // 可以添加一个字段用于排序（如果需要的话）
    // private int sortOrder;

    // 注意：如果使用前端ID做主键，就不需要 @GeneratedValue 了
}