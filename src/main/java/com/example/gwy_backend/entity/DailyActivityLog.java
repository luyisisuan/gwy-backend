package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime; // 可能需要 LocalDateTime 如果要精确时间

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_activity_log",
       indexes = {
           @Index(name = "idx_activity_date", columnList = "activityDate", unique = true), // activityDate 唯一
           @Index(name = "idx_log_date", columnList = "logDate") // logDate 普通索引
       })
public class DailyActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate activityDate; // 记录的活动日期 (保持唯一)

    // **MODIFIED:** 添加 logDate 字段，并映射到数据库的 log_date 列
    // 假设它也需要存储日期，并且不允许为空
    @Column(name = "log_date", nullable = false) // 明确映射到 log_date 列
    private LocalDate logDate;

    @Column(nullable = false)
    private long totalOnlineSeconds = 0;

    // (可选) 如果需要区分创建和更新时间，可以添加创建时间戳
    // @Column(nullable = false, updatable = false)
    // private LocalDateTime createdAt;

    // (可选) 保留最后更新时间戳
    // @Column(nullable = false)
    // private LocalDateTime lastUpdatedAt;

    // 如果 logDate 和 activityDate 总是一样，可以在创建时一起设置
    @PrePersist
    protected void onCreate() {
        LocalDate now = LocalDate.now();
        if (activityDate == null) { // 如果 activityDate 未设置 (理论上不应发生)
            activityDate = now;
        }
        if (logDate == null) { // 如果 logDate 未设置
            logDate = activityDate; // 让 logDate 默认等于 activityDate
        }
        // if (createdAt == null) createdAt = LocalDateTime.now();
        // lastUpdatedAt = LocalDateTime.now();
    }

    // 如果允许更新 totalOnlineSeconds 以外的字段，可以保留 @PreUpdate
    // @PreUpdate
    // protected void onUpdate() {
    //     lastUpdatedAt = LocalDateTime.now();
    // }

    // 提供一个方便的构造函数 (Lombok 的 @AllArgsConstructor 会生成所有字段的)
    public DailyActivityLog(LocalDate activityDate) {
        this.activityDate = activityDate;
        this.logDate = activityDate; // 默认 logDate 等于 activityDate
        this.totalOnlineSeconds = 0;
    }
}