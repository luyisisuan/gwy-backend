package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.Course; // <<< 导入新的实体类 Course
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // 导入 List
// import java.util.Optional; // Optional 会由 JpaRepository 提供

@Repository
// <<< 接口名称改为 CourseRepository，泛型参数改为 Course
public interface CourseRepository extends JpaRepository<Course, Long> {

    // <<< 移除 findByCourseKey 方法 >>>

    // 提供按最后更新时间降序排序的方法 (方便前端显示最新的)
    List<Course> findAllByOrderByLastUpdatedDesc();

    // 如果需要按分类查找（示例）
    // List<Course> findByCategoryIgnoreCaseOrderByLastUpdatedDesc(String category);

    // JpaRepository 已提供 findById, findAll, save, deleteById 等基础方法
}