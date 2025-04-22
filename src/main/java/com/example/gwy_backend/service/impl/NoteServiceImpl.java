package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.factory.NoteFactory; // 假设这个工厂是必须的
import com.example.gwy_backend.repository.NoteEntryRepository;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
// import java.time.Instant; // 这个 Service 实现本身没直接用到 Instant，但保持导入无害

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteEntryRepository noteEntryRepository;
    private final NoteFactory noteFactory; // 假设 NoteFactory 会处理 noteKey 和 content

    @Autowired
    public NoteServiceImpl(NoteEntryRepository noteEntryRepository, NoteFactory noteFactory) {
        this.noteEntryRepository = noteEntryRepository;
        this.noteFactory = noteFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteEntry> getAllNotesSortedByTimestamp() {
        log.info("Fetching all notes sorted by timestamp.");
        // Repository 方法会基于 Instant 类型的 timestamp 字段进行排序
        return noteEntryRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    @Transactional
    public NoteEntry createNote(NoteEntry noteEntry) throws IllegalArgumentException {
        log.info("Attempting to create new note with key: {}", noteEntry.getNoteKey());
        // 检查内容是否为空
        if (!StringUtils.hasText(noteEntry.getContent())) {
            log.warn("Note creation failed: Content cannot be empty.");
            throw new IllegalArgumentException("Note content cannot be empty.");
        }
        // 使用 NoteFactory 创建实例 (工厂内部或 NoteEntry 的 @PrePersist 会设置 timestamp)
        NoteEntry noteToSave = noteFactory.createNote(noteEntry.getNoteKey(), noteEntry.getContent());
        // 确保 timestamp 最终会被 @PrePersist 设置为 Instant.now()

        log.debug("Saving new note entry prepared by factory: {}", noteToSave);
        return noteEntryRepository.save(noteToSave);
    }

    @Override
    @Transactional
    public boolean deleteNoteById(Long id) {
        log.warn("Attempting to delete note with ID: {}", id);
        if (id == null) {
            log.error("Cannot delete note with null ID.");
            return false;
        }
        // 先检查是否存在，避免不必要的删除尝试
        if (noteEntryRepository.existsById(id)) {
            try {
                noteEntryRepository.deleteById(id); // 执行删除
                log.info("Note with ID: {} deleted successfully.", id);
                return true; // 删除成功
            } catch (Exception e) {
                 // 捕获可能的删除异常
                 log.error("Error occurred while deleting note with ID: {}", id, e);
                 return false; // 删除失败
            }
        } else {
            log.warn("Note with ID: {} not found, cannot delete.", id);
            return false; // 记录不存在，删除失败
        }
    }
}