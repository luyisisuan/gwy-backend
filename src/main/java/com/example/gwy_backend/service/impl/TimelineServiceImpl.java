package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.TimelineTask;
import com.example.gwy_backend.repository.TimelineTaskRepository;
import com.example.gwy_backend.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimelineServiceImpl implements TimelineService {

    private final TimelineTaskRepository taskRepository;

    @Autowired
    public TimelineServiceImpl(TimelineTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<TimelineTask>> getAllTasksGroupedByPhase() {
        // 获取所有任务并按 phase 分组
        List<TimelineTask> allTasks = taskRepository.findAllByOrderByPhaseAsc(); // 按阶段排序获取
        return allTasks.stream()
                .collect(Collectors.groupingBy(TimelineTask::getPhase));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimelineTask> getAllTasksList() {
        return taskRepository.findAllByOrderByPhaseAsc(); // 直接返回排序列表
    }


    @Override
    @Transactional
    public void initializeTasks(List<TimelineTask> initialTasks) {
        // 检查数据库是否已存在任务，如果存在则不进行初始化
        if (taskRepository.count() > 0) {
            System.out.println("Tasks already initialized, skipping.");
            return;
        }
        // 批量保存初始任务列表
        if (initialTasks != null && !initialTasks.isEmpty()) {
            taskRepository.saveAll(initialTasks);
            System.out.println("Initialized " + initialTasks.size() + " timeline tasks.");
        }
    }

    @Override
    @Transactional
    public boolean updateTaskCompletion(String taskId, boolean completed) {
        // 调用 Repository 的更新方法
        // updateTaskCompletion 返回受影响的行数，大于 0 表示更新成功
        int updatedRows = taskRepository.updateTaskCompletion(taskId, completed);
        return updatedRows > 0;
    }

    // (可选) 批量更新实现
    // @Override
    // @Transactional
    // public int updateTasksCompletion(List<String> taskIds, boolean completed) {
    //     if (taskIds == null || taskIds.isEmpty()) {
    //         return 0;
    //     }
    //     return taskRepository.updateCompletionStatusForIds(taskIds, completed);
    // }
}