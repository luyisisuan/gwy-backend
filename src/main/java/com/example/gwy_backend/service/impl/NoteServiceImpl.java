package com.example.gwy_backend.service.impl;

// --- 确认以下导入路径是否与你的项目结构一致 ---
import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.factory.NoteFactory; // 需要 NoteFactory
import com.example.gwy_backend.repository.NoteEntryRepository;
import com.example.gwy_backend.service.NoteService;
// --- 导入必要的 Java 和 Spring 类 ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 需要事务注解
import org.springframework.util.StringUtils; // 需要 StringUtils
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteEntryRepository noteEntryRepository;
    private final NoteFactory noteFactory; // 注入 Factory

    @Autowired
    public NoteServiceImpl(NoteEntryRepository noteEntryRepository, NoteFactory noteFactory) {
        this.noteEntryRepository = noteEntryRepository;
        this.noteFactory = noteFactory;
    }

    /**
     * 获取所有笔记，按创建时间降序排列。
     */
    @Override
    @Transactional(readOnly = true)
    public List<NoteEntry> getAllNotesSortedByTimestamp() {
        log.info("Fetching all notes sorted by timestamp.");
        return noteEntryRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * 根据 noteKey 获取单个笔记条目实体。
     * 注意：由于 noteKey 不再唯一，此方法可能返回多个结果中的第一个，
     * 或者如果 findByNoteKey 被移除，则此方法也应移除或修改。
     * 这里保留它以匹配之前的接口，但可能需要调整。
     */
    // @Override
    // @Transactional(readOnly = true)
    // public Optional<NoteEntry> getNoteEntryByKey(String noteKey) {
    //     log.debug("Fetching note entry by key: {}", noteKey);
    //     // 假设 Repository 中仍然保留 findByNoteKey，但它可能返回多个
    //     // return noteEntryRepository.findFirstByNoteKeyOrderByTimestampDesc(noteKey); // 获取最新的一个
    //     // 或者如果 Repository 移除了 findByNoteKey，则需要注释或删除此方法
    //      return noteEntryRepository.findByNoteKey(noteKey); // 保持与 Repository 一致
    // }

    /**
     * 创建一条新的笔记记录。
     */
    @Override
    @Transactional
    public NoteEntry createNote(NoteEntry noteEntry) {
        log.info("Attempting to create new note with key: {}", noteEntry.getNoteKey());
        // 验证 content
        if (!StringUtils.hasText(noteEntry.getContent())) {
            throw new IllegalArgumentException("Note content cannot be empty.");
        }
        // 使用 Factory 创建实体
        NoteEntry noteToSave = noteFactory.createNote(noteEntry.getNoteKey(), noteEntry.getContent());
        log.debug("Saving new note entry: {}", noteToSave);
        // timestamp 会通过 @PrePersist 自动设置
        return noteEntryRepository.save(noteToSave);
    }

    // saveOrUpdateNote 方法已移除
}