package com.example.gwy_backend.factory; // <<< 新建 factory 包

import com.example.gwy_backend.entity.NoteEntry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 工厂类，用于创建 NoteEntry 对象。
 * 使用 @Component 使其成为 Spring 管理的 Bean。
 */
@Component
public class NoteFactory {

    /**
     * 创建笔记实体。可以根据 noteKey 添加特定逻辑。
     * @param noteKey 笔记的 key 或类型 (例如 "general", "notes-phase1")
     * @param content 笔记内容
     * @return 创建好的 NoteEntry 对象 (未设置 ID 和 timestamp)
     */
    public NoteEntry createNote(String noteKey, String content) {
        NoteEntry note = new NoteEntry();

        // 设置 noteKey，如果没有提供，默认为 "general"
        note.setNoteKey(StringUtils.hasText(noteKey) ? noteKey.trim() : "general");

        // 设置 content (不能为空，Service 层已做校验)
        note.setContent(content);

        // timestamp 和 id 将由 JPA 在持久化时处理 (@PrePersist, @GeneratedValue)
        return note;
    }
}