package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.NoteEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 如果没有其他 @Query 可以移除
import org.springframework.stereotype.Repository;
import java.util.List;
// import java.util.Optional; // 不再需要 Optional

@Repository
public interface NoteEntryRepository extends JpaRepository<NoteEntry, Long> {

    // Optional<NoteEntry> findByNoteKey(String noteKey); // <<< 移除这个方法

    // 获取所有笔记条目，并按创建时间降序排列。
    @Query("SELECT ne FROM NoteEntry ne ORDER BY ne.timestamp DESC") // 保留这个排序方法
    List<NoteEntry> findAllByOrderByTimestampDesc();

}