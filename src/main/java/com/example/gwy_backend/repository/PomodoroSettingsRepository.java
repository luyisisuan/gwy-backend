package com.example.gwy_backend.repository; // <<< 确认包名与你的项目一致

import com.example.gwy_backend.entity.PomodoroSettings; // <<< 确认 Entity 路径正确
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository 接口用于处理 PomodoroSettings 实体的数据库操作。
 * 继承 JpaRepository 以获得标准的 CRUD 功能。
 */
@Repository // 标记这是一个 Spring Data Repository Bean
public interface PomodoroSettingsRepository extends JpaRepository<PomodoroSettings, Long> { // 实体类: PomodoroSettings, 主键类型: Long

    /**
     * 根据 settingsKey 查找番茄钟设置。
     * Spring Data JPA 会根据方法名自动生成查询实现。
     * "findBy" 表示查询操作。
     * "SettingsKey" 表示查询条件基于实体的 settingsKey 字段。
     *
     * @param settingsKey 要查找的设置的唯一标识符
     * @return 包含找到的 PomodoroSettings 的 Optional，如果找不到则为空 Optional
     */
    Optional<PomodoroSettings> findBySettingsKey(String settingsKey);

    // 不需要其他方法，因为 JpaRepository 已经提供了 save(), findById(), findAll(), deleteById() 等
}