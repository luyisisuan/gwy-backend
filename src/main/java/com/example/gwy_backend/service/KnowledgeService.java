package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.KnowledgeItem;
import java.util.List;
import java.util.Optional;

public interface KnowledgeService {
    List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm);
    Optional<KnowledgeItem> getKnowledgeItemById(Long id);
    KnowledgeItem addKnowledgeItem(KnowledgeItem knowledgeItem);

    // --- >>> 添加更新方法的声明 <<< ---
    Optional<KnowledgeItem> updateKnowledgeItem(Long id, KnowledgeItem knowledgeItemDetails);
    // --- <<< 结束 <<< ---

    boolean deleteKnowledgeItem(Long id);
}