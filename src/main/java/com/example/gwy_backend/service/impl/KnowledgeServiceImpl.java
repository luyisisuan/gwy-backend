package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.KnowledgeItem;
import com.example.gwy_backend.repository.KnowledgeItemRepository;
import com.example.gwy_backend.service.KnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime; // 可能需要更新时间戳
import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);
    private final KnowledgeItemRepository knowledgeItemRepository;

    @Autowired
    public KnowledgeServiceImpl(KnowledgeItemRepository knowledgeItemRepository) {
        this.knowledgeItemRepository = knowledgeItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm) {
        // ... (现有代码不变) ...
        boolean hasCategory = StringUtils.hasText(category) && !"all".equalsIgnoreCase(category);
        boolean hasSearchTerm = StringUtils.hasText(searchTerm);

        if (hasCategory && hasSearchTerm) {
            log.info("Searching by category '{}' and term '{}' using safe repository method", category, searchTerm);
            return knowledgeItemRepository.findByCategoryAndSearchTermOrderByTimestampDesc(category, searchTerm);
        } else if (hasCategory) {
            log.info("Searching by category '{}' using safe repository method", category);
            return knowledgeItemRepository.findByCategoryIgnoreCaseOrderByTimestampDesc(category);
        } else if (hasSearchTerm) {
            log.info("Searching by term '{}' using safe repository method", searchTerm);
            return knowledgeItemRepository.searchByTermOrderByTimestampDesc(searchTerm);
        } else {
            log.info("Fetching all knowledge items");
            return knowledgeItemRepository.findAllByOrderByTimestampDesc();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeItem> getKnowledgeItemById(Long id) {
        log.info("Fetching knowledge item by id: {}", id);
        return knowledgeItemRepository.findById(id);
    }

    @Override
    @Transactional
    public KnowledgeItem addKnowledgeItem(KnowledgeItem knowledgeItem) {
        log.info("Adding new knowledge item with title: {}", knowledgeItem.getTitle());
        knowledgeItem.setId(null);
        if (knowledgeItem.getTags() == null) {
            knowledgeItem.setTags(List.of());
        }
        // timestamp 会由 @PrePersist 自动设置
        return knowledgeItemRepository.save(knowledgeItem);
    }

    // --- >>> 实现更新方法 <<< ---
    @Override
    @Transactional
    public Optional<KnowledgeItem> updateKnowledgeItem(Long id, KnowledgeItem knowledgeItemDetails) {
        log.info("Attempting to update knowledge item with id: {}", id);
        return knowledgeItemRepository.findById(id)
                .map(existingItem -> {
                    log.info("Found item to update. Updating fields for id: {}", id);
                    existingItem.setTitle(knowledgeItemDetails.getTitle());
                    existingItem.setCategory(knowledgeItemDetails.getCategory());
                    existingItem.setContent(knowledgeItemDetails.getContent());
                    existingItem.setTags(knowledgeItemDetails.getTags() != null ? knowledgeItemDetails.getTags() : List.of());
                    existingItem.setExternalLink(knowledgeItemDetails.getExternalLink());
                    existingItem.setLinkedFile(knowledgeItemDetails.getLinkedFile());
                    // 通常，我们不在这里手动更新 timestamp，让 @PreUpdate (如果配置了) 或数据库触发器处理，
                    // 或者如果业务逻辑要求，可以显式设置，例如：
                    // existingItem.setTimestamp(LocalDateTime.now()); // 如果需要记录更新时间
                    KnowledgeItem updated = knowledgeItemRepository.save(existingItem);
                    log.info("Successfully updated knowledge item with id: {}", id);
                    return updated;
                })
                .map(Optional::ofNullable) // 确保即使 save 返回 null（理论上不应该），也能正确处理
                .orElseGet(() -> {
                    log.warn("Knowledge item with id: {} not found for update.", id);
                    return Optional.empty();
                });
    }
    // --- <<< 更新方法结束 <<< ---

    @Override
    @Transactional
    public boolean deleteKnowledgeItem(Long id) {
        log.info("Attempting to delete knowledge item with id: {}", id);
        if (knowledgeItemRepository.existsById(id)) {
            knowledgeItemRepository.deleteById(id);
            log.info("Successfully deleted knowledge item with id: {}", id);
            return true;
        }
        log.warn("Knowledge item with id: {} not found for deletion", id);
        return false;
    }
}