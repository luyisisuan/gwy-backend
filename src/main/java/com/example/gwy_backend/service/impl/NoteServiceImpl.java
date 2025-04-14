package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.factory.NoteFactory;
import com.example.gwy_backend.repository.NoteEntryRepository;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
// 移除了 Optional

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteEntryRepository noteEntryRepository;
    private final NoteFactory noteFactory;

    @Autowired
    public NoteServiceImpl(NoteEntryRepository noteEntryRepository, NoteFactory noteFactory) {
        this.noteEntryRepository = noteEntryRepository;
        this.noteFactory = noteFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteEntry> getAllNotesSortedByTimestamp() {
        log.info("Fetching all notes sorted by timestamp.");
        // **MODIFIED:** 确认调用正确的 Repository 方法
        return noteEntryRepository.findAllByOrderByTimestampDesc();
    }

    // getNoteEntryByKey 方法已移除

    @Override
    @Transactional
    public NoteEntry createNote(NoteEntry noteEntry) throws IllegalArgumentException {
        log.info("Attempting to create new note with key: {}", noteEntry.getNoteKey());
        if (!StringUtils.hasText(noteEntry.getContent())) {
            throw new IllegalArgumentException("Note content cannot be empty.");
        }
        NoteEntry noteToSave = noteFactory.createNote(noteEntry.getNoteKey(), noteEntry.getContent());
        log.debug("Saving new note entry: {}", noteToSave);
        return noteEntryRepository.save(noteToSave);
    }

    /**
     * **ADDED:** 实现删除笔记的方法。
     */
    @Override
    @Transactional // 需要事务进行删除操作
    public boolean deleteNoteById(Long id) {
        log.warn("Attempting to delete note with ID: {}", id);
        if (id == null) {
            log.error("Cannot delete note with null ID.");
            return false;
        }
        if (noteEntryRepository.existsById(id)) { // 先检查是否存在
            try {
                noteEntryRepository.deleteById(id); // 执行删除
                log.info("Note with ID: {} deleted successfully.", id);
                return true; // 删除成功
            } catch (Exception e) {
                 // 捕获可能的删除异常 (例如数据库约束)
                 log.error("Error occurred while deleting note with ID: {}", id, e);
                 return false; // 删除失败
            }
        } else {
            log.warn("Note with ID: {} not found, cannot delete.", id);
            return false; // 记录不存在，删除失败
        }
    }
}