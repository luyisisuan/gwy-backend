package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.CourseTracker;
import com.example.gwy_backend.repository.CourseTrackerRepository;
import com.example.gwy_backend.service.CourseTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CourseTrackerServiceImpl implements CourseTrackerService {

    private final CourseTrackerRepository courseTrackerRepository;

    @Autowired
    public CourseTrackerServiceImpl(CourseTrackerRepository courseTrackerRepository) {
        this.courseTrackerRepository = courseTrackerRepository;
    }

    @Override
    @Transactional // 可能涉及写入操作 (创建默认值)
    public CourseTracker getCourseTracker(String courseKey) {
        // 查找，如果找不到，则创建一个新的默认 CourseTracker 并保存
        return courseTrackerRepository.findByCourseKey(courseKey)
                .orElseGet(() -> {
                    CourseTracker defaultTracker = new CourseTracker();
                    defaultTracker.setCourseKey(courseKey);
                    // 其他字段使用实体类中定义的默认值
                    return courseTrackerRepository.save(defaultTracker);
                });
    }

    @Override
    @Transactional
    public Optional<CourseTracker> updateCourseTracker(String courseKey, Map<String, Object> updates) {
        Optional<CourseTracker> existingTrackerOptional = courseTrackerRepository.findByCourseKey(courseKey);

        if (existingTrackerOptional.isPresent()) {
            CourseTracker trackerToUpdate = existingTrackerOptional.get();
            boolean changed = false;

            // 根据 updates Map 中的键更新对应字段
            if (updates.containsKey("courseName") && updates.get("courseName") instanceof String value) {
                trackerToUpdate.setCourseName(value);
                changed = true;
            }
            if (updates.containsKey("courseLink") && updates.get("courseLink") instanceof String value) {
                trackerToUpdate.setCourseLink(value);
                changed = true;
            }
            if (updates.containsKey("totalLessons") && updates.get("totalLessons") instanceof Number value) {
                // 确保 totalLessons 至少为 1
                trackerToUpdate.setTotalLessons(Math.max(1, value.intValue()));
                changed = true;
            }
            if (updates.containsKey("completedLessons") && updates.get("completedLessons") instanceof Number value) {
                // 确保 completed 不超过 total，且不小于 0
                int newCompleted = Math.max(0, value.intValue());
                trackerToUpdate.setCompletedLessons(Math.min(trackerToUpdate.getTotalLessons(), newCompleted));
                changed = true;
            }
            if (updates.containsKey("notes") && updates.get("notes") instanceof String value) {
                trackerToUpdate.setNotes(value);
                changed = true;
            }

            // 只有当确实有字段被更新时才保存
            if (changed) {
                // lastUpdated 会通过 @PreUpdate 自动更新
                return Optional.of(courseTrackerRepository.save(trackerToUpdate));
            } else {
                // 如果没有字段更新，直接返回现有的 tracker
                return Optional.of(trackerToUpdate);
            }
        } else {
            // 如果 courseKey 不存在，则不进行更新，返回空 Optional
            return Optional.empty();
        }
    }
}