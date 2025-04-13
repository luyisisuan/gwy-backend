package com.example.gwy_backend.service; // 替换为你的包名

import com.example.gwy_backend.entity.StudyGoal;
import java.util.List;
import java.util.Optional;

public interface StudyGoalService {

    List<StudyGoal> getAllGoals(); // 获取所有目标

    Optional<StudyGoal> getGoalById(Long id); // 根据 ID 获取目标 (Optional 防止空指针)

    StudyGoal addGoal(StudyGoal goal); // 添加新目标

    Optional<StudyGoal> updateGoal(Long id, StudyGoal goalDetails); // 更新目标

    Optional<StudyGoal> toggleGoalCompletion(Long id); // 切换完成状态

    boolean deleteGoal(Long id); // 删除目标 (返回是否成功)
}