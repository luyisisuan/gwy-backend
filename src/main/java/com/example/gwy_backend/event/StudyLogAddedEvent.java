package com.example.gwy_backend.event; // <<< 确认包名

import com.example.gwy_backend.entity.StudyLog; // <<< 确认实体路径
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert; // 用于参数校验

/**
 * 表示学习日志已成功添加的领域事件。
 */
public class StudyLogAddedEvent extends ApplicationEvent {

    private final StudyLog studyLog;

    /**
     * 创建一个新的 StudyLogAddedEvent.
     * @param source 事件源 (通常是发布事件的服务实例，例如 PomodoroServiceImpl)
     * @param studyLog 新添加的 StudyLog 对象 (不能为 null)
     */
    public StudyLogAddedEvent(Object source, StudyLog studyLog) {
        super(source);
        Assert.notNull(studyLog, "StudyLog cannot be null for StudyLogAddedEvent"); // 添加校验
        this.studyLog = studyLog;
    }

    /**
     * 获取与此事件关联的 StudyLog 实体。
     * @return 已保存的 StudyLog 实体
     */
    public StudyLog getStudyLog() {
        return studyLog;
    }
}