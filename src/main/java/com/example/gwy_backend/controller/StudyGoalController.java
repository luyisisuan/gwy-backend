package com.example.gwy_backend.controller; // <<< 已修改 (假设你有 controller 子包)

import com.example.gwy_backend.entity.StudyGoal; // <<< 确认 entity 路径正确
import com.example.gwy_backend.service.StudyGoalService; // <<< 确认 service 路径正确
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 导入 Web 相关注解

import java.util.List;

@RestController // 标记这是一个 RESTful 控制器，所有方法默认返回 JSON
@RequestMapping("/api/goals") // 定义基础路径，所有请求都基于 /api/goals
// @CrossOrigin // 临时允许所有跨域请求 (开发时方便，生产环境需要更精细配置)
public class StudyGoalController {

    private final StudyGoalService studyGoalService;

    @Autowired
    public StudyGoalController(StudyGoalService studyGoalService) {
        this.studyGoalService = studyGoalService;
    }

    // --- API Endpoints ---

    // GET /api/goals - 获取所有目标
    @GetMapping
    public ResponseEntity<List<StudyGoal>> getAllGoals() {
        List<StudyGoal> goals = studyGoalService.getAllGoals();
        return ResponseEntity.ok(goals); // 返回 200 OK 和目标列表
    }

    // GET /api/goals/{id} - 获取单个目标
    @GetMapping("/{id}")
    public ResponseEntity<StudyGoal> getGoalById(@PathVariable Long id) {
        return studyGoalService.getGoalById(id)
                .map(ResponseEntity::ok) // 如果找到，返回 200 OK 和目标
                .orElse(ResponseEntity.notFound().build()); // 如果找不到，返回 404 Not Found
    }

    // POST /api/goals - 添加新目标
    @PostMapping
    public ResponseEntity<StudyGoal> addGoal(@RequestBody StudyGoal goal) {
        // @RequestBody 将请求体中的 JSON 自动映射到 StudyGoal 对象
        StudyGoal createdGoal = studyGoalService.addGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal); // 返回 201 Created 和新创建的目标
    }

    // PUT /api/goals/{id} - 更新目标 (整个替换)
    @PutMapping("/{id}")
    public ResponseEntity<StudyGoal> updateGoal(@PathVariable Long id, @RequestBody StudyGoal goalDetails) {
        return studyGoalService.updateGoal(id, goalDetails)
                .map(ResponseEntity::ok) // 更新成功，返回 200 OK 和更新后的目标
                .orElse(ResponseEntity.notFound().build()); // 找不到，返回 404
    }

    // PATCH /api/goals/{id}/toggle - 切换完成状态 (更符合 PATCH 语义)
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<StudyGoal> toggleGoal(@PathVariable Long id) {
        return studyGoalService.toggleGoalCompletion(id)
                .map(ResponseEntity::ok) // 切换成功，返回 200 OK 和更新后的目标
                .orElse(ResponseEntity.notFound().build()); // 找不到，返回 404
    }

    // DELETE /api/goals/{id} - 删除目标
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        if (studyGoalService.deleteGoal(id)) {
            return ResponseEntity.noContent().build(); // 删除成功，返回 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 找不到，返回 404
        }
    }
}