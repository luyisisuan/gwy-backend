package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.TimelineTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // 需要
import org.springframework.data.jpa.repository.Query; // 需要
import org.springframework.data.repository.query.Param; // 需要
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// 主键类型改为 String
public interface TimelineTaskRepository extends JpaRepository<TimelineTask, String> {

    // 获取所有任务，可以按 phase 或 sortOrder 排序 (如果添加了排序字段)
    List<TimelineTask> findAllByOrderByPhaseAsc(); // 按阶段排序

    // 根据阶段查找任务
    List<TimelineTask> findByPhase(String phase);

    // (可选) 批量更新任务状态的方法 (可能比单个更新更高效，但稍复杂)
    // @Modifying
    // @Query("UPDATE TimelineTask t SET t.completed = :completed WHERE t.id IN :ids")
    // int updateCompletionStatusForIds(@Param("ids") List<String> ids, @Param("completed") boolean completed);

    // 根据 ID 更新单个任务的完成状态 (更常用)
    @Modifying
    @Query("UPDATE TimelineTask t SET t.completed = :completed WHERE t.id = :id")
    int updateTaskCompletion(@Param("id") String id, @Param("completed") boolean completed);
}