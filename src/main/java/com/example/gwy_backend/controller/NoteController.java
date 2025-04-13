package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <<< 导入 HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// 移除 Map 和 Optional
// import java.util.Map;
// import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger log = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // GET /api/notes - 获取所有笔记 (按创建时间倒序)
    @GetMapping
    public ResponseEntity<List<NoteEntry>> getAllNotesSorted() { // <<< 修改方法名和返回类型
        log.info("Received request to get all notes.");
        List<NoteEntry> notes = noteService.getAllNotesSortedByTimestamp(); // <<< 调用新 Service 方法
        return ResponseEntity.ok(notes);
    }

    // GET /api/notes/{id} - 获取单个笔记实体 (如果需要)
    // @GetMapping("/{id}")
    // public ResponseEntity<NoteEntry> getNoteEntryById(@PathVariable Long id) { ... }

    // POST /api/notes - 创建新的笔记记录
    @PostMapping // <<< 改为 POST
    public ResponseEntity<?> createNote(@RequestBody NoteEntry noteEntry) { // <<< 接收 NoteEntry 对象
        log.info("Received request to create note with key: {}", noteEntry.getNoteKey());
        // 基本验证 (Service 层也会验证 content)
        if (noteEntry.getContent() == null || noteEntry.getContent().trim().isEmpty()) {
            log.warn("Note content cannot be empty.");
            // 可以返回更详细的错误信息
            return ResponseEntity.badRequest().body("Note content cannot be empty.");
        }
        try {
            NoteEntry createdEntry = noteService.createNote(noteEntry); // <<< 调用创建方法
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry); // <<< 返回 201 Created
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create note: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating note", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating note.");
        }
    }

    // --- 移除 PUT /api/notes/{noteKey} 端点 ---
    // @PutMapping("/{noteKey}")
    // public ResponseEntity<NoteEntry> saveNote(...) { ... }
}