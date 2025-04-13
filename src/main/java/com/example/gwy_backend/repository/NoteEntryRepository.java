package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.NoteEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
// 移除 Optional 导入，因为 findByNoteKey 没了
// import java.util.Optional;

@Repository
public interface NoteEntryRepository extends JpaRepository<NoteEntry, Long> {

    // Optional<NoteEntry> findByNoteKey(String noteKey); // <<< 移除

    /**
     * 获取所有笔记条目，并按创建时间降序排列。
     * @return 按 timestamp 降序排列的 NoteEntry 列表
     */
    List<NoteEntry> findAllByOrderByTimestampDesc(); // <<< 确保这个方法存在

    // 可以添加按 noteKey 筛选并排序的方法 (如果需要)
    // List<NoteEntry> findByNoteKeyOrderByTimestampDesc(String noteKey);
}