package com.example.gwy_backend.controller;

// <<< 导入新的 Service, DTOs, CourseDto 列表, ResponseEntity, Valid 注解等 >>>
import com.example.gwy_backend.dto.CourseDto;
import com.example.gwy_backend.dto.CreateCourseDto;
import com.example.gwy_backend.dto.UpdateCourseDto;
import com.example.gwy_backend.service.CourseService;
import jakarta.validation.Valid; // <<< 用于触发 DTO 校验
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <<< 导入 HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // <<< 用于类级别校验（可选）
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses") // <<< 更新基础路径为 /api/courses
@Validated // <<< 在类级别启用校验（可选，确保 @Valid 生效）
// <<< 类名改为 CourseController >>>
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService; // <<< 注入新的 CourseService

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // --- 新增 API 端点 ---

    /**
     * GET /api/courses : 获取所有课程列表
     * @return 包含所有课程 DTO 的列表和 200 OK 状态码。
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        log.info("接收到请求：获取所有课程");
        List<CourseDto> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses); // 直接返回列表和 200 OK
    }

    /**
     * GET /api/courses/{id} : 根据 ID 获取单个课程详情
     * @param id 课程 ID (从路径中获取)
     * @return 如果找到，返回课程 DTO 和 200 OK；否则返回 404 Not Found。
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        log.info("接收到请求：获取课程 ID: {}", id);
        Optional<CourseDto> courseDtoOptional = courseService.findCourseById(id);
        // 使用 map 和 orElseGet 来构造响应
        return courseDtoOptional
                .map(ResponseEntity::ok) // 如果存在，返回 200 OK 和 DTO
                .orElseGet(() -> { // 如果不存在
                    log.warn("未找到课程 ID: {}", id);
                    return ResponseEntity.notFound().build(); // 返回 404 Not Found
                });
    }

    /**
     * POST /api/courses : 创建新课程
     * @param createCourseDto 包含新课程数据的请求体 DTO (使用 @Valid 进行校验)
     * @return 创建成功后返回新课程的 DTO 和 201 Created 状态码。
     *         如果校验失败，Spring Boot 的异常处理器通常会返回 400 Bad Request。
     */
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CreateCourseDto createCourseDto) {
        log.info("接收到请求：创建新课程，名称: {}", createCourseDto.getName());
        try {
            CourseDto createdCourse = courseService.createCourse(createCourseDto);
            // 创建成功，返回 201 Created 和新创建的资源 DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (IllegalArgumentException e) {
             // Service 层如果抛出校验异常 (虽然主要校验在 DTO)
             log.error("创建课程时发生错误: {}", e.getMessage());
             // 返回 400 Bad Request (或者更具体的错误处理)
             // 注意：@Valid 失败通常由 Spring Boot 的默认异常处理器处理，这里是捕获 Service 层可能的异常
             return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PATCH /api/courses/{id} : 更新指定课程的部分信息
     * @param id 要更新的课程 ID
     * @param updateCourseDto 包含要更新字段的请求体 DTO (使用 @Valid 进行校验)
     * @return 如果更新成功，返回更新后的课程 DTO 和 200 OK；
     *         如果课程不存在，返回 404 Not Found；
     *         如果校验失败，返回 400 Bad Request。
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long id, @Valid @RequestBody UpdateCourseDto updateCourseDto) {
        log.info("接收到请求：更新课程 ID: {}", id);
        try {
            Optional<CourseDto> updatedCourseDtoOptional = courseService.updateCourse(id, updateCourseDto);
            return updatedCourseDtoOptional
                    .map(ResponseEntity::ok) // 更新成功，返回 200 OK 和更新后的 DTO
                    .orElseGet(() -> { // 课程未找到
                        log.warn("更新失败，未找到课程 ID: {}", id);
                        return ResponseEntity.notFound().build(); // 返回 404 Not Found
                    });
        } catch (IllegalArgumentException e) {
            log.error("更新课程 ID: {} 时发生错误: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build(); // 返回 400 Bad Request
        }
    }

    /**
     * DELETE /api/courses/{id} : 删除指定课程
     * @param id 要删除的课程 ID
     * @return 如果删除成功，返回 204 No Content；
     *         如果课程不存在或删除失败，返回 404 Not Found。
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        log.warn("接收到请求：删除课程 ID: {}", id); // 删除操作通常用 warn 级别日志
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 删除成功，返回 204 No Content
        } else {
            // 可能是未找到或删除过程中出错（Service 层已记录错误）
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
    }

    // <<< 移除旧的 get 和 patch 方法 >>>
}