package com.example.gwy_backend.entity; // 替换为你的包名

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data; // Lombok: 自动生成 getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity // 告诉 JPA 这是一个数据库实体类，会映射到一张表
@Data   // Lombok: 自动生成常用方法
@NoArgsConstructor // Lombok: 生成无参构造函数 (JPA 需要)
@AllArgsConstructor // Lombok: 生成包含所有字段的构造函数
public class StudyGoal {

    @Id // 标记这是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键自增生成策略
    private Long id;

    private String text; // 目标内容

    private boolean completed; // 是否完成
}