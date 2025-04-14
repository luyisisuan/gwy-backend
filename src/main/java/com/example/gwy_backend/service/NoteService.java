package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.NoteEntry;
import java.util.List;
// import java.util.Optional; // 不再需要 Optional

public interface NoteService {

    /**
     * 获取所有笔记，按创建时间降序排列。
     * @return NoteEntry 列表
     */
    List<NoteEntry> getAllNotesSortedByTimestamp();

    /**
     * 根据 ID 获取单个笔记条目实体 (可选)。
     */
    // Optional<NoteEntry> getNoteEntryById(Long id); // 如果需要按 ID 获取可以保留或添加这个

    // --- 移除 getNoteEntryByKey ---
    // Optional<NoteEntry> getNoteEntryByKey(String noteKey);

    /**
     * 创建一条新的笔记记录。
     */
    NoteEntry createNote(NoteEntry noteEntry);
}