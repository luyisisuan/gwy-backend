package com.example.gwy_backend.dto;

import com.example.gwy_backend.entity.Course.CourseStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank; // 非空字符串校验
import jakarta.validation.constraints.NotNull; // 非空对象校验
import jakarta.validation.constraints.Size;     // 长度校验
import lombok.Data;
import org.hibernate.validator.constraints.URL; // URL 格式校验

@Data
public class CreateCourseDto {

    @NotBlank(message = "课程名称不能为空") // 必须有内容
    @Size(max = 255, message = "课程名称不能超过 255 个字符")
    private String name;

    @Size(max = 2048, message = "课程链接不能超过 2048 个字符")
    // @URL(protocol = "https", message = "课程链接必须是有效的 HTTPS URL") // 可选：强制 HTTPS
    @URL(message = "课程链接格式不正确") // 基本 URL 校验
    private String link; // 链接是可选的，但如果提供，必须是 URL 格式

    @NotNull(message = "总节数不能为空") // 必须提供总节数
    @Min(value = 1, message = "总节数必须大于等于 1")
    private Integer totalLessons = 1; // 提供默认值 1

    @Min(value = 0, message = "已完成节数不能小于 0")
    private Integer completedLessons = 0; // 已完成节数是可选的，默认为 0

    // 创建时可以指定状态，如果未指定，Service 层可以设置默认值 NOT_STARTED
    private CourseStatus status;

    @Size(max = 100, message = "分类名称不能超过 100 个字符")
    private String category; // 分类是可选的
}