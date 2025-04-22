package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.dto.CourseDto; // <<< 导入 DTO
import com.example.gwy_backend.dto.CreateCourseDto;
import com.example.gwy_backend.dto.UpdateCourseDto;
import com.example.gwy_backend.entity.Course; // <<< 导入实体
import com.example.gwy_backend.entity.Course.CourseStatus; // <<< 导入枚举
import com.example.gwy_backend.repository.CourseRepository; // <<< 导入新的 Repository
import com.example.gwy_backend.service.CourseService; // <<< 实现新的 Service 接口
import org.slf4j.Logger; // 推荐使用 SLF4J 进行日志记录
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils; // 用于对象属性复制
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
// <<< 类名和实现的接口更新 >>>
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository; // <<< 注入新的 Repository

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true) // 查询操作设为只读事务
    public List<CourseDto> findAllCourses() {
        log.info("查找所有课程...");
        return courseRepository.findAllByOrderByLastUpdatedDesc() // 调用按时间排序的方法
                .stream() // 使用 Stream API 进行转换
                .map(this::convertToDto) // 对每个实体调用转换方法
                .collect(Collectors.toList()); // 收集结果为 List
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDto> findCourseById(Long id) {
        log.info("根据 ID 查找课程: {}", id);
        return courseRepository.findById(id) // 使用 JpaRepository 的 findById
                .map(this::convertToDto); // 如果找到，则转换为 DTO
    }

    @Override
    @Transactional // 创建操作需要写事务
    public CourseDto createCourse(CreateCourseDto createCourseDto) {
        log.info("创建新课程: {}", createCourseDto.getName());
        Course newCourse = new Course();
        // 使用 BeanUtils 或手动将 DTO 属性复制到实体
        BeanUtils.copyProperties(createCourseDto, newCourse, "status"); // 复制属性，可能需要手动处理特殊字段如 status

        // 设置默认状态（如果 DTO 未提供）
        newCourse.setStatus(Optional.ofNullable(createCourseDto.getStatus()).orElse(CourseStatus.NOT_STARTED));

        // 校验已完成课时不能超过总课时
        if (newCourse.getCompletedLessons() > newCourse.getTotalLessons()) {
           log.warn("创建课程 '{}' 时，已完成课时 {} 不能超过总课时 {}",
                    newCourse.getName(), newCourse.getCompletedLessons(), newCourse.getTotalLessons());
           newCourse.setCompletedLessons(newCourse.getTotalLessons()); // 自动修正
        }

        Course savedCourse = courseRepository.save(newCourse); // 保存实体
        log.info("新课程创建成功, ID: {}", savedCourse.getId());
        return convertToDto(savedCourse); // 返回转换后的 DTO
    }

    @Override
    @Transactional // 更新操作需要写事务
    public Optional<CourseDto> updateCourse(Long id, UpdateCourseDto updateCourseDto) {
        log.info("尝试更新课程 ID: {}", id);
        Optional<Course> existingCourseOptional = courseRepository.findById(id);

        if (existingCourseOptional.isEmpty()) {
            log.warn("更新失败，未找到课程 ID: {}", id);
            return Optional.empty(); // 课程不存在，返回空 Optional
        }

        Course courseToUpdate = existingCourseOptional.get();
        boolean updated = false; // 标记是否有实际更新

        // --- 逐个检查 DTO 中的字段并更新实体 ---
        if (updateCourseDto.getName() != null) {
            // 添加额外的校验，例如不允许更新为空字符串
            if (updateCourseDto.getName().isBlank()) {
                log.warn("课程名称不能为空字符串。");
                // 可以选择抛出异常或忽略此更新
                // throw new IllegalArgumentException("课程名称不能为空字符串。");
            } else {
                courseToUpdate.setName(updateCourseDto.getName());
                updated = true;
            }
        }
        if (updateCourseDto.getLink() != null) { // 允许将链接设置为空字符串或 null
            courseToUpdate.setLink(updateCourseDto.getLink().isBlank() ? null : updateCourseDto.getLink());
            updated = true;
        }
        // 临时存储更新的 total 和 completed，以便相互校验
        Integer newTotal = updateCourseDto.getTotalLessons();
        Integer newCompleted = updateCourseDto.getCompletedLessons();
        boolean totalUpdated = false;
        boolean completedUpdated = false;

        if (newTotal != null) {
            courseToUpdate.setTotalLessons(Math.max(1, newTotal)); // 保证 >= 1
            totalUpdated = true;
            updated = true;
        }
        if (newCompleted != null) {
            courseToUpdate.setCompletedLessons(Math.max(0, newCompleted)); // 保证 >= 0
            completedUpdated = true;
            updated = true;
        }

        // --- 校验 completed 不能超过 total ---
        // 如果 completed 更新了，或者 total 更新了导致需要调整 completed
        if (completedUpdated || (totalUpdated && courseToUpdate.getCompletedLessons() > courseToUpdate.getTotalLessons())) {
            courseToUpdate.setCompletedLessons(
                Math.min(courseToUpdate.getTotalLessons(), courseToUpdate.getCompletedLessons())
            );
            // 确认 completedLessons 是否真的改变了
            if (newCompleted == null || newCompleted != courseToUpdate.getCompletedLessons()) {
                 updated = true; // 如果修正导致值变化，标记为更新
            }
        }


        if (updateCourseDto.getStatus() != null) {
            courseToUpdate.setStatus(updateCourseDto.getStatus());
            updated = true;
        }
        if (updateCourseDto.getCategory() != null) {
            courseToUpdate.setCategory(updateCourseDto.getCategory().isBlank() ? null : updateCourseDto.getCategory());
            updated = true;
        }

        // 如果有任何字段被实际更新，则保存
        if (updated) {
            Course savedCourse = courseRepository.save(courseToUpdate); // @PreUpdate 会自动更新 lastUpdated
            log.info("课程 ID: {} 更新成功。", id);
            return Optional.of(convertToDto(savedCourse));
        } else {
            log.info("课程 ID: {} 无需更新。", id);
            // 没有更新也返回当前状态的 DTO
            return Optional.of(convertToDto(courseToUpdate));
        }
    }

    @Override
    @Transactional // 删除操作需要写事务
    public boolean deleteCourse(Long id) {
        log.warn("尝试删除课程 ID: {}", id);
        if (courseRepository.existsById(id)) { // 先检查是否存在
            try {
                courseRepository.deleteById(id);
                log.info("课程 ID: {} 删除成功。", id);
                return true;
            } catch (Exception e) {
                log.error("删除课程 ID: {} 时出错。", id, e);
                // 可以考虑抛出自定义异常或返回 false
                return false;
            }
        } else {
            log.warn("删除失败，未找到课程 ID: {}", id);
            return false;
        }
    }


    // --- 私有辅助方法：将 Course 实体转换为 CourseDto ---
    private CourseDto convertToDto(Course course) {
        CourseDto dto = new CourseDto();
        BeanUtils.copyProperties(course, dto); // 复制基础属性

        // 计算进度百分比
        int total = Math.max(1, course.getTotalLessons()); // 防止除以零
        int completed = Math.max(0, Math.min(course.getCompletedLessons(), total));
        dto.setProgressPercentage(Math.round(((float) completed / total) * 100));

        return dto;
    }
}