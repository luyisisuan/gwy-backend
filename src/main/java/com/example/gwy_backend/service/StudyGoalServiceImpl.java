package com.example.gwy_backend.service; // <<< 已修改 (假设你有 service.impl 子包)

import com.example.gwy_backend.entity.StudyGoal; // <<< 确认 entity 路径正确
import com.example.gwy_backend.repository.StudyGoalRepository; // <<< 确认 repository 路径正确
import com.example.gwy_backend.service.StudyGoalService; // <<< 确认 service 接口路径正确
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.util.List;
import java.util.Optional;

@Service // 标记这是一个服务层组件 (Spring Bean)
public class StudyGoalServiceImpl implements StudyGoalService {

    private final StudyGoalRepository studyGoalRepository;

    // 使用构造函数注入 Repository (推荐方式)
    @Autowired
    public StudyGoalServiceImpl(StudyGoalRepository studyGoalRepository) {
        this.studyGoalRepository = studyGoalRepository;
    }

    @Override
    @Transactional(readOnly = true) // 只读事务，优化性能
    public List<StudyGoal> getAllGoals() {
        // 调用 Repository 的 findAll 方法获取所有数据
        // Spring Data JPA 默认按 ID 升序，如果需要特定排序可以在 Repository 加方法
        return studyGoalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudyGoal> getGoalById(Long id) {
        return studyGoalRepository.findById(id);
    }

    @Override
    @Transactional // 默认读写事务
    public StudyGoal addGoal(StudyGoal goal) {
        // 清除 ID，确保是新增操作，让数据库生成新 ID
        goal.setId(null);
        // 调用 Repository 的 save 方法保存数据
        return studyGoalRepository.save(goal);
    }

    @Override
    @Transactional
    public Optional<StudyGoal> updateGoal(Long id, StudyGoal goalDetails) {
        // 先根据 ID 查找现有目标
        return studyGoalRepository.findById(id).map(existingGoal -> {
            // 如果找到，更新其字段
            existingGoal.setText(goalDetails.getText());
            existingGoal.setCompleted(goalDetails.isCompleted());
            // 保存更新后的目标
            return studyGoalRepository.save(existingGoal);
        }); // 如果找不到，map 操作不会执行，返回 Optional.empty()
    }

    @Override
    @Transactional
    public Optional<StudyGoal> toggleGoalCompletion(Long id) {
        // 先根据 ID 查找现有目标
        return studyGoalRepository.findById(id).map(goal -> {
            // 切换完成状态
            goal.setCompleted(!goal.isCompleted());
            // 保存更新后的目标
            return studyGoalRepository.save(goal);
        }); // 找不到则返回 Optional.empty()
    }


    @Override
    @Transactional
    public boolean deleteGoal(Long id) {
        // 检查目标是否存在
        if (studyGoalRepository.existsById(id)) {
            // 存在则删除
            studyGoalRepository.deleteById(id);
            return true; // 删除成功
        }
        return false; // 目标不存在，删除失败
    }
}