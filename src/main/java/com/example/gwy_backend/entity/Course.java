package com.example.gwy_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Jakarta Bean Validation 注解 (用在 DTO)
import jakarta.validation.constraints.Size;     // Jakarta Bean Validation 注解 (用在 DTO)
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant; // <<< 导入 Instant，替换 LocalDateTime

@Entity
@Data // Lombok: 自动生成 getter, setter 等
@NoArgsConstructor // Lombok: 无参构造
@AllArgsConstructor // Lombok: 全参构造
@Table(name = "course", // 重命名数据库表
       indexes = { @Index(name = "idx_course_last_updated", columnList = "lastUpdated DESC") }) // 为 lastUpdated 添加索引，方便排序
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 数据库自增 ID
    private Long id;

    // @NotBlank (校验放在 DTO 中)
    @Size(max = 255) // 数据库层面的长度限制 (示例)
    @Column(nullable = false) // 数据库层面非空约束
    private String name; // 课程名称 (必填)

    @Size(max = 2048) // 数据库层面的 URL 长度限制 (示例)
    // @URL (校验放在 DTO 中)
    private String link; // 课程链接 (可选)

    @Column(nullable = false)
    // @Min(1) (校验放在 DTO 中)
    private int totalLessons = 1; // 总节数，默认为 1，且数据库层面不应小于 1

    @Column(nullable = false)
    // @Min(0) (校验放在 DTO 中)
    private int completedLessons = 0; // 已完成节数，默认为 0

    @Enumerated(EnumType.STRING) // 将枚举存储为字符串形式 (如 "IN_PROGRESS")
    @Column(nullable = false, length = 50) // 状态不能为空，限制长度
    private CourseStatus status = CourseStatus.NOT_STARTED; // <<< 新增状态字段，默认“未开始”

    @Column(length = 100) // 可选的分类字段
    private String category;

    @Column(nullable = false, updatable = false) // 创建时间：非空，创建后不可修改
    private Instant createdAt; // <<< 新增创建时间字段

    @Column(nullable = false) // 最后更新时间：非空
    private Instant lastUpdated; // <<< 类型改为 Instant

    // --- 生命周期回调：在持久化或更新前执行 ---
    @PrePersist // 插入数据库前执行
    protected void onPrePersist() {
        Instant now = Instant.now();
        this.createdAt = now; // 设置创建时间
        this.lastUpdated = now; // 设置初始的最后更新时间
        // 确保初始数据有效性
        this.totalLessons = Math.max(1, this.totalLessons);
        this.completedLessons = Math.max(0, Math.min(this.completedLessons, this.totalLessons));
        // 如果创建时未指定状态，设置为默认值
        if (this.status == null) {
            this.status = CourseStatus.NOT_STARTED;
        }
    }

    @PreUpdate // 更新数据库前执行
    protected void onPreUpdate() {
        this.lastUpdated = Instant.now(); // 更新“最后更新时间”
        // 确保更新后的数据有效性
        this.totalLessons = Math.max(1, this.totalLessons);
        this.completedLessons = Math.max(0, Math.min(this.completedLessons, this.totalLessons));
    }

    // --- 内部枚举定义课程状态 ---
    // （推荐将这个枚举放到单独的文件 `CourseStatus.java` 中）
    public enum CourseStatus {
        NOT_STARTED("未开始"),
        IN_PROGRESS("进行中"),
        COMPLETED("已完成"),
        ON_HOLD("已搁置");

        private final String displayName;

        CourseStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}