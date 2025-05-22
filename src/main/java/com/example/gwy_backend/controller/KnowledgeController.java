package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.KnowledgeItem;
import com.example.gwy_backend.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/knowledge") // 基础路径 /api/knowledge
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Autowired
    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    // GET /api/knowledge - 获取知识条目 (支持按分类和搜索词筛选)
    @GetMapping
    public ResponseEntity<List<KnowledgeItem>> getKnowledgeItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search
    ) {
        List<KnowledgeItem> items = knowledgeService.getKnowledgeItems(category, search);
        return ResponseEntity.ok(items);
    }

    // GET /api/knowledge/{id} - 获取单个知识条目
    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeItem> getKnowledgeItemById(@PathVariable Long id) {
        return knowledgeService.getKnowledgeItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/knowledge - 添加新的知识条目
    @PostMapping
    public ResponseEntity<KnowledgeItem> addKnowledgeItem(@RequestBody KnowledgeItem knowledgeItem) {
        if (knowledgeItem.getTitle() == null || knowledgeItem.getTitle().trim().isEmpty() ||
                knowledgeItem.getCategory() == null || knowledgeItem.getCategory().trim().isEmpty() ||
                knowledgeItem.getContent() == null || knowledgeItem.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        KnowledgeItem createdItem = knowledgeService.addKnowledgeItem(knowledgeItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    // --- >>> 添加这个 PUT 方法来处理更新 <<< ---
    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeItem> updateKnowledgeItem(@PathVariable Long id, @RequestBody KnowledgeItem knowledgeItemDetails) {
        // 可以在这里添加验证，例如 knowledgeItemDetails 的必要字段不能为空

        Optional<KnowledgeItem> updatedItemOptional = knowledgeService.updateKnowledgeItem(id, knowledgeItemDetails);

        return updatedItemOptional
                .map(ResponseEntity::ok) // 如果更新成功，返回 200 OK 和更新后的条目
                .orElse(ResponseEntity.notFound().build()); // 如果未找到要更新的条目，返回 404 Not Found
    }
    // --- <<< 更新方法结束 <<< ---


    // DELETE /api/knowledge/{id} - 删除知识条目
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKnowledgeItem(@PathVariable Long id) {
        if (knowledgeService.deleteKnowledgeItem(id)) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
}