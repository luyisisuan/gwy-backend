package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.CourseTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourseTrackerRepository extends JpaRepository<CourseTracker, Long> {

    // 根据 courseKey 查找课程追踪信息
    Optional<CourseTracker> findByCourseKey(String courseKey);
}