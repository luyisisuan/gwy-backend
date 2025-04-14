package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.NoteEntry;
import java.util.List;
// 移除了 Optional

public interface NoteService {

    /**
     * 获取所有笔记，按创建时间降序排列。
     * @return NoteEntry 列表
     */
    List<NoteEntry> getAllNotesSortedByTimestamp();

    /**
     * 创建一条新的笔记记录。
     * @param noteEntry 包含 content 和可选 noteKey 的新笔记对象 (ID 应为 null)
     * @return 保存后的 NoteEntry 实体 (包含生成的 ID 和时间戳)
     * @throws IllegalArgumentException 如果 content 为空
     */
    NoteEntry createNote(NoteEntry noteEntry) throws IllegalArgumentException;

    /**
     * **ADDED:** 根据 ID 删除笔记记录。
     * @param id 要删除的笔记 ID
     * @return 如果删除成功返回 true，如果记录不存在返回 false
     */
    boolean deleteNoteById(Long id); // <<< 添加删除方法声明
}