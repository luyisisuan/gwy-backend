package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.NoteEntry;
import java.util.List;
// 移除 Map 和 Optional
// import java.util.Map;
// import java.util.Optional;

public interface NoteService {

    /**
     * 获取所有笔记，按创建时间降序排列。
     * @return NoteEntry 列表
     */
    List<NoteEntry> getAllNotesSortedByTimestamp(); // <<< 修改名称和返回类型

    /**
     * 根据 ID 获取单个笔记条目实体 (可选)。
     * @param id 笔记的 ID
     * @return 包含 NoteEntry 实体的 Optional
     */
    // Optional<NoteEntry> getNoteEntryById(Long id); // <<< 可以保留或移除，取决于是否需要单条查看

    /**
     * 创建一条新的笔记记录。
     * @param noteEntry 包含 content 和可选 noteKey 的新笔记对象 (ID 应为 null)
     * @return 保存后的 NoteEntry 实体 (包含生成的 ID 和时间戳)
     */
    NoteEntry createNote(NoteEntry noteEntry); // <<< 修改为创建方法
}