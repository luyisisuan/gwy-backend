package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.NoteEntry;
import com.example.gwy_backend.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     */
    @GetMapping
    public ResponseEntity<List<NoteEntry>> getAllNotesSorted() {
        log.info("Received request to get all notes.");
        try {
            List<NoteEntry> notes = noteService.getAllNotesSortedByTimestamp();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            log.error("Error fetching all notes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 创建一条新的笔记记录。
     */
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody NoteEntry noteEntry) {
        log.info("Received request to create note with key: {}", noteEntry.getNoteKey());
        if (!StringUtils.hasText(noteEntry.getContent())) {
            log.warn("Note content cannot be empty.");
            return ResponseEntity.badRequest().body("Note content cannot be empty.");
        }
        try {
            NoteEntry createdEntry = noteService.createNote(noteEntry);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create note due to invalid argument: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating note", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while creating the note.");
        }
    }

    /**
     * **ADDED:** 删除指定 ID 的笔记记录。
     * @param id 要删除的笔记 ID (从路径获取)
     * @return 成功返回 204 No Content，失败返回 404 Not Found 或 500 Internal Server Error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.warn("Received request to delete note with ID: {}", id);
        try {
            boolean deleted = noteService.deleteNoteById(id);
            if (deleted) {
                return ResponseEntity.noContent().build(); // 204: 成功，无内容返回
            } else {
                // Service 层返回 false 通常意味着记录未找到
                return ResponseEntity.notFound().build(); // 404: 资源未找到
            }
        } catch (Exception e) { // 捕获 Service 层可能抛出的其他异常
             log.error("Error deleting note with ID: {}", id, e);
             return ResponseEntity.internalServerError().build(); // 500: 服务器内部错误
        }
    }
}