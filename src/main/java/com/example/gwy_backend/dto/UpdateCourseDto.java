package com.example.gwy_backend.dto;

import com.example.gwy_backend.entity.Course.CourseStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size; // 导入 Size
import lombok.Data;
import org.hibernate.validator.constraints.URL; // 导入 URL

@Data
public class UpdateCourseDto {

    // 对于 PATCH，所有字段都是可选的

    @Size(max = 255, message = "课程名称不能超过 255 个字符")
    // 如果更新时 name 字段存在，它不应该是空字符串 (可以在 Service 层校验，或使用 @NotBlank 但允许 null)
    private String name;

    @Size(max = 2048, message = "课程链接不能超过 2048 个字符")
    @URL(message = "课程链接格式不正确")
    private String link;

    @Min(value = 1, message = "总节数必须大于等于 1")
    private Integer totalLessons;

    @Min(value = 0, message = "已完成节数不能小于 0")
    private Integer completedLessons;

    private CourseStatus status;

    @Size(max = 100, message = "分类名称不能超过 100 个字符")
    private String category;
}