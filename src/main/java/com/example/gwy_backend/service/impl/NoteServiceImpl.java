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

import java.time.Instant; // <<< 引入 Instant，如果 NoteEntry 使用 Instant
import java.util.List;
import java.util.Optional; // <<< 引入 Optional

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteEntryRepository noteEntryRepository;
    private final NoteFactory noteFactory; // 假设 NoteFactory 对更新操作没有直接作用，除非你想通过它来验证或预处理

    @Autowired
    public NoteServiceImpl(NoteEntryRepository noteEntryRepository, NoteFactory noteFactory) {
        this.noteEntryRepository = noteEntryRepository;
        this.noteFactory = noteFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteEntry> getAllNotesSortedByTimestamp() {
        log.info("Fetching all notes sorted by timestamp.");
        return noteEntryRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    @Transactional
    public NoteEntry createNote(NoteEntry noteEntry) throws IllegalArgumentException {
        log.info("Attempting to create new note with key: {}", noteEntry.getNoteKey());
        if (!StringUtils.hasText(noteEntry.getContent())) {
            log.warn("Note creation failed: Content cannot be empty.");
            throw new IllegalArgumentException("Note content cannot be empty.");
        }
        // 使用 NoteFactory 创建实例 (工厂内部或 NoteEntry 的 @PrePersist 会设置 timestamp)
        // 确保工厂创建的对象是用于持久化的新实例
        NoteEntry noteToSave = noteFactory.createNote(noteEntry.getNoteKey(), noteEntry.getContent());
        // 如果noteEntry中还有其他字段需要传递给工厂，请在createNote方法签名中添加

        log.debug("Saving new note entry prepared by factory: {}", noteToSave);
        return noteEntryRepository.save(noteToSave);
    }

    // --- >>> ADDED: 实现更新笔记的方法 <<< ---
    @Override
    @Transactional
    public Optional<NoteEntry> updateNote(Long id, NoteEntry noteDetails) throws IllegalArgumentException {
        log.info("Attempting to update note with ID: {}", id);
        if (id == null) {
            log.warn("Cannot update note with null ID.");
            throw new IllegalArgumentException("Note ID for update cannot be null.");
        }
        if (noteDetails == null || !StringUtils.hasText(noteDetails.getContent())) {
            log.warn("Note update failed for ID {}: Content cannot be empty.", id);
            throw new IllegalArgumentException("Note content for update cannot be empty.");
        }

        // 1. 查找现有的笔记
        Optional<NoteEntry> existingNoteOptional = noteEntryRepository.findById(id);

        if (existingNoteOptional.isPresent()) {
            NoteEntry existingNote = existingNoteOptional.get();
            log.debug("Found note with ID {} to update. Current details: {}", id, existingNote);

            // 2. 更新字段
            // 你需要从 noteDetails 中获取要更新的字段，并设置到 existingNote 对象上
            // 假设你的 NoteEntry 有 setContent 和 setNoteKey 方法
            existingNote.setContent(noteDetails.getContent());
            if (StringUtils.hasText(noteDetails.getNoteKey())) { // 只有当 noteDetails 中提供了 noteKey 才更新
                existingNote.setNoteKey(noteDetails.getNoteKey());
            } else {
                // 如果业务逻辑允许 noteKey 保持不变或默认为 'general'，可以在这里处理
                 if (existingNote.getNoteKey() == null) { // 例如，如果原来是null，可以设个默认值
                     existingNote.setNoteKey("general");
                 }
            }
            // 如果你的 NoteEntry 实体类有 @PreUpdate 注解来更新时间戳，则不需要手动设置。
            // 否则，如果需要记录更新时间，你可能需要手动设置：
            // existingNote.setTimestamp(Instant.now()); // 假设 NoteEntry 中的 timestamp 是 Instant 类型

            // 3. 保存更新后的笔记
            try {
                NoteEntry updatedNote = noteEntryRepository.save(existingNote);
                log.info("Successfully updated note with ID: {}. New details: {}", id, updatedNote);
                return Optional.of(updatedNote);
            } catch (Exception e) {
                log.error("Error saving updated note with ID: {}", id, e);
                // 可以选择抛出自定义异常或返回 Optional.empty()
                return Optional.empty(); // 或者根据需要处理，例如抛出运行时异常
            }
        } else {
            log.warn("Note with ID: {} not found for update.", id);
            return Optional.empty(); // 表示未找到
        }
    }
    // --- >>> END ADDED <<< ---

    @Override
    @Transactional
    public boolean deleteNoteById(Long id) {
        log.warn("Attempting to delete note with ID: {}", id); // Log level can be info if preferred
        if (id == null) {
            log.error("Cannot delete note with null ID.");
            return false;
        }
        if (noteEntryRepository.existsById(id)) {
            try {
                noteEntryRepository.deleteById(id);
                log.info("Note with ID: {} deleted successfully.", id);
                return true;
            } catch (Exception e) {
                 log.error("Error occurred while deleting note with ID: {}", id, e);
                 return false;
            }
        } else {
            log.warn("Note with ID: {} not found, cannot delete.", id);
            return false;
        }
    }
}