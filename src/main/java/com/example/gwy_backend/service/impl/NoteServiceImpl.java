package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.repository.NoteEntryRepository;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// 移除 Map
// import java.util.Map;
import java.util.Optional;
// 移除 Collectors
// import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    private final NoteEntryRepository noteEntryRepository;

    @Autowired
    public NoteServiceImpl(NoteEntryRepository noteEntryRepository) {
        this.noteEntryRepository = noteEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteEntry> getAllNotesSortedByTimestamp() { // <<< 修改实现
        log.info("Fetching all notes sorted by timestamp.");
        return noteEntryRepository.findAllByOrderByTimestampDesc(); // <<< 调用排序方法
    }

    // @Override
    // @Transactional(readOnly = true)
    // public Optional<NoteEntry> getNoteEntryById(Long id) { ... } // <<< 如果需要，实现它

    @Override
    @Transactional
    public NoteEntry createNote(NoteEntry noteEntry) { // <<< 修改实现
        log.info("Creating new note with key: {}", noteEntry.getNoteKey());
        noteEntry.setId(null); // 确保是新增
        // content 不能为空 (可以在 Controller 或这里加验证)
        if (noteEntry.getContent() == null || noteEntry.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be empty.");
        }
        // timestamp 会通过 @PrePersist 自动设置
        return noteEntryRepository.save(noteEntry); // <<< 保存新笔记
    }

    // --- 移除 saveOrUpdateNote 方法 ---
    // @Override
    // @Transactional
    // public NoteEntry saveOrUpdateNote(String noteKey, String content) { ... }
}