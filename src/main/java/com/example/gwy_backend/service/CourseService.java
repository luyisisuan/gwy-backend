package com.example.gwy_backend.service;

// <<< 导入新的 DTO 和 CourseDto 列表 >>>
import com.example.gwy_backend.dto.CourseDto;
import com.example.gwy_backend.dto.CreateCourseDto;
import com.example.gwy_backend.dto.UpdateCourseDto;

import java.util.List;
import java.util.Optional;

// <<< 接口名称改为 CourseService >>>
public interface CourseService {

    /**
     * 获取所有课程，按最后更新时间降序排序。
     * @return 包含所有课程 DTO 的列表。
     */
    List<CourseDto> findAllCourses();

    /**
     * 根据 ID 查找单个课程。
     * @param id 课程 ID。
     * @return 包含课程 DTO 的 Optional，如果找不到则为空。
     */
    Optional<CourseDto> findCourseById(Long id);

    /**
     * 创建一个新的课程。
     * @param createCourseDto 包含新课程数据的 DTO。
     * @return 创建成功的课程的 DTO。
     * @throws IllegalArgumentException 如果输入数据无效 (虽然校验主要在 Controller 层)。
     */
    CourseDto createCourse(CreateCourseDto createCourseDto);

    /**
     * 更新指定 ID 的课程。只更新 DTO 中非 null 的字段。
     * @param id 要更新的课程 ID。
     * @param updateCourseDto 包含要更新字段的 DTO。
     * @return 更新成功后的课程 DTO 的 Optional，如果课程不存在则为空。
     * @throws IllegalArgumentException 如果更新数据导致状态不一致 (如 completed > total)。
     */
    Optional<CourseDto> updateCourse(Long id, UpdateCourseDto updateCourseDto);

    /**
     * 删除指定 ID 的课程。
     * @param id 要删除的课程 ID。
     * @return 如果删除成功返回 true，否则返回 false。
     */
    boolean deleteCourse(Long id);

}