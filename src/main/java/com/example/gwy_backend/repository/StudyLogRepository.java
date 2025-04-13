package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.StudyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // <<< 导入 @Modifying
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

    // 获取所有日志，按开始时间倒序
    List<StudyLog> findAllByOrderByStartTimeDesc();

    // 获取指定时间范围内的日志 (示例)
    List<StudyLog> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime start, LocalDateTime end);

    // 计算指定时间范围内的总时长 (使用 JPQL SUM)
    @Query("SELECT COALESCE(SUM(s.durationSeconds), 0) FROM StudyLog s WHERE s.startTime >= :start AND s.startTime < :end")
    long sumDurationSecondsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 删除所有的 StudyLog 记录。
     * 使用 @Modifying 注解表明这是一个更新/删除操作。
     * 使用 @Query 定义 JPQL 删除语句。
     */
    @Modifying // <<< 添加 @Modifying 注解
    @Query("DELETE FROM StudyLog sl") // <<< JPQL 删除语句 (sl 是别名，可以省略)
    void deleteAllLogs(); // <<< 添加方法声明

    // 可以添加按用户 ID 查询的方法 (如果实现用户系统)
    // List<StudyLog> findByUserIdOrderByStartTimeDesc(Long userId);
}