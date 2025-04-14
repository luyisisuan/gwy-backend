package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils; // <<< 建议导入 StringUtils
import org.springframework.web.bind.annotation.*;

import java.util.List;
// 移除了 Map 和 Optional 的导入

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private static final Logger log = LoggerFactory.getLogger(NoteController.class);
    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 获取所有笔记记录，按创建时间倒序排列。
     * @return 包含 NoteEntry 实体的列表 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<NoteEntry>> getAllNotesSorted() {
        log.info("Received request to get all notes.");
        List<NoteEntry> notes = noteService.getAllNotesSortedByTimestamp();
        return ResponseEntity.ok(notes);
    }

    /**
     * 创建一条新的笔记记录。
     * 请求体应包含带有 "content" 和可选 "noteKey" 的 JSON 对象。
     * @param noteEntry 从请求体映射的 NoteEntry 对象
     * @return 创建成功的 NoteEntry 实体 (201 Created) 或错误响应 (400/500)
     */
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody NoteEntry noteEntry) {
        log.info("Received request to create note with key: {}", noteEntry.getNoteKey());
        // 使用 StringUtils.hasText 进行更健壮的空值/空白检查
        if (!StringUtils.hasText(noteEntry.getContent())) {
            log.warn("Note content cannot be empty.");
            return ResponseEntity.badRequest().body("Note content cannot be empty.");
        }
        try {
            NoteEntry createdEntry = noteService.createNote(noteEntry);
            // 返回 201 Created 状态码和创建的实体
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (IllegalArgumentException e) { // Service 层抛出的验证异常
            log.warn("Failed to create note due to invalid argument: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) { // 捕获其他潜在的运行时异常
            log.error("Unexpected error creating note", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while creating the note.");
        }
    }

    // GET /api/notes/{id} 端点（如果需要按 ID 获取单条）可以保留或添加
    // PUT /api/notes/{noteKey} 端点已移除，因为是日志式添加

}