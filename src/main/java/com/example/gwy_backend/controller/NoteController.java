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
import java.util.Optional; // <<< 引入 Optional

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

    // --- >>> ADDED: 更新指定 ID 的笔记记录 <<< ---
    /**
     * 更新指定 ID 的笔记记录。
     * @param id 要更新的笔记 ID (从路径获取)
     * @param noteDetails 包含更新后数据的笔记对象 (从请求体获取)
     * @return 成功返回 200 OK 和更新后的笔记，失败返回 404 Not Found, 400 Bad Request 或 500 Internal Server Error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody NoteEntry noteDetails) {
        log.info("Received request to update note with ID: {}", id);

        // 基本验证
        if (!StringUtils.hasText(noteDetails.getContent())) {
            log.warn("Note content for update cannot be empty. ID: {}", id);
            return ResponseEntity.badRequest().body("Note content cannot be empty for update.");
        }
        // 你可能还想验证 noteKey 或其他字段

        try {
            Optional<NoteEntry> updatedNoteOptional = noteService.updateNote(id, noteDetails);

            return updatedNoteOptional
                    .map(updatedNote -> {
                        log.info("Successfully updated note with ID: {}", id);
                        return ResponseEntity.ok(updatedNote); // 200 OK with updated note
                    })
                    .orElseGet(() -> {
                        log.warn("Note with ID: {} not found for update.", id);
                        return ResponseEntity.notFound().build(); // 404 Not Found
                    });
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update note ID {} due to invalid argument: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating note with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while updating the note.");
        }
    }
    // --- >>> END ADDED <<< ---

    /**
     * 删除指定 ID 的笔记记录。
     * @param id 要删除的笔记 ID (从路径获取)
     * @return 成功返回 204 No Content，失败返回 404 Not Found 或 500 Internal Server Error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.warn("Received request to delete note with ID: {}", id); // Changed to warn as it's a destructive op
        try {
            boolean deleted = noteService.deleteNoteById(id);
            if (deleted) {
                log.info("Successfully deleted note with ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Note with ID: {} not found for deletion.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
             log.error("Error deleting note with ID: {}", id, e);
             return ResponseEntity.internalServerError().build();
        }
    }
}