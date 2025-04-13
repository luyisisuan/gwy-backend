package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.TimelineTask;
import java.util.List;
import java.util.Map; // 用于返回按阶段分组的任务

public interface TimelineService {

    // 获取所有任务，按阶段分组返回 Map<phase, List<Task>>
    Map<String, List<TimelineTask>> getAllTasksGroupedByPhase();

    // 获取所有任务的平铺列表 (按阶段排序)
    List<TimelineTask> getAllTasksList();

    // 初始化或重置任务列表 (如果后端还没有数据)
    void initializeTasks(List<TimelineTask> initialTasks);

    // 更新单个任务的完成状态
    boolean updateTaskCompletion(String taskId, boolean completed);

    // (可选) 批量更新任务状态
    // int updateTasksCompletion(List<String> taskIds, boolean completed);
}