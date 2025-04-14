package com.example.gwy_backend.listener; // <<< 确认包名

import com.example.gwy_backend.entity.StudyLog;
import com.example.gwy_backend.event.StudyLogAddedEvent; // <<< 导入事件
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener; // <<< 导入注解
import org.springframework.scheduling.annotation.Async; // <<< 可选：异步处理
import org.springframework.stereotype.Component;

/**
 * 监听并处理学习日志相关事件的组件。
 */
@Component // 标记为 Spring Bean
public class StudyLogEventListener {

    private static final Logger log = LoggerFactory.getLogger(StudyLogEventListener.class);

    // 未来可以注入其他 Service，例如用于更新统计缓存或成就系统
    // @Autowired
    // private SomeOtherService someOtherService;

    /**
     * 处理学习日志添加事件。
     * 使用 @EventListener 注解订阅 StudyLogAddedEvent。
     * 使用 @Async (可选) 使事件处理在单独的线程中异步执行，避免阻塞发布者。
     * @param event 包含新添加日志的事件对象
     */
    @EventListener
    // @Async // 如果希望异步处理，需要先在启动类上加 @EnableAsync
    public void handleStudyLogAdded(StudyLogAddedEvent event) {
        StudyLog addedLog = event.getStudyLog();
        // 添加简单的延迟，模拟处理耗时（仅用于演示异步效果）
        // try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        log.info("[Listener] Received StudyLogAddedEvent: ID={}, Activity='{}', Duration={}s. Source={}",
                addedLog.getId(),
                addedLog.getActivity(),
                addedLog.getDurationSeconds(),
                event.getSource().getClass().getSimpleName()); // 打印事件源类名

        // 在这里执行与日志添加相关的解耦操作:
        // - 更新相关统计缓存（如果未使用实时计算）
        // - 检查是否触发成就
        // - 发送通知等...
        // 例如: someOtherService.processNewLog(addedLog);

        // 目前仅打印日志
    }

     // 可以添加处理其他事件的方法
     // @EventListener
     // public void handleAnotherAppEvent(AnotherEvent event) { ... }
}