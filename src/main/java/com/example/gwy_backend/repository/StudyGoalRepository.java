package com.example.gwy_backend.repository; // 替换为你的包名

import com.example.gwy_backend.entity.StudyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 标记这是一个 Spring Bean，用于数据访问
public interface StudyGoalRepository extends JpaRepository<StudyGoal, Long> {
    // JpaRepository<实体类名, 主键类型>
    // Spring Data JPA 会自动提供常用的 CRUD 方法：
    // findAll(), findById(), save(), deleteById(), ...
    // 你也可以根据需要在这里定义自定义查询方法 (遵循命名约定)
}