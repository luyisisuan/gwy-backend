package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.KnowledgeItem;
import java.util.List;
import java.util.Optional;

public interface KnowledgeService {

    // 根据筛选条件获取知识条目列表
    // category 和 searchTerm 都可以为 null 或空
    List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm);

    Optional<KnowledgeItem> getKnowledgeItemById(Long id);

    KnowledgeItem addKnowledgeItem(KnowledgeItem knowledgeItem);

    boolean deleteKnowledgeItem(Long id);
}