package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.KnowledgeItem;
import com.example.gwy_backend.repository.KnowledgeItemRepository;
import com.example.gwy_backend.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // 导入 Spring 的 StringUtils

import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeItemRepository knowledgeItemRepository;

    @Autowired
    public KnowledgeServiceImpl(KnowledgeItemRepository knowledgeItemRepository) {
        this.knowledgeItemRepository = knowledgeItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm) {
        // 使用 Spring 的 StringUtils.hasText() 判断字符串是否非空且包含非空白字符
        boolean hasCategory = StringUtils.hasText(category) && !"all".equalsIgnoreCase(category); // 假设 'all' 表示不过滤
        boolean hasSearchTerm = StringUtils.hasText(searchTerm);

        if (hasCategory && hasSearchTerm) {
            // 同时有分类和搜索词
            return knowledgeItemRepository.findByCategoryAndSearchTermOrderByTimestampDesc(category, searchTerm);
        } else if (hasCategory) {
            // 只有分类
            return knowledgeItemRepository.findByCategoryIgnoreCaseOrderByTimestampDesc(category);
        } else if (hasSearchTerm) {
            // 只有搜索词
            return knowledgeItemRepository.searchByTermOrderByTimestampDesc(searchTerm);
        } else {
            // 没有筛选条件，获取所有
            return knowledgeItemRepository.findAllByOrderByTimestampDesc();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KnowledgeItem> getKnowledgeItemById(Long id) {
        return knowledgeItemRepository.findById(id);
    }

    @Override
    @Transactional
    public KnowledgeItem addKnowledgeItem(KnowledgeItem knowledgeItem) {
        knowledgeItem.setId(null); // 确保是新增
        // timestamp 会通过 @PrePersist 自动设置
        // 处理一下 tags，确保不为 null (虽然 @ElementCollection 可能处理，但显式处理更安全)
        if (knowledgeItem.getTags() == null) {
            knowledgeItem.setTags(List.of()); // 或者 new ArrayList<>()
        }
        return knowledgeItemRepository.save(knowledgeItem);
    }

    @Override
    @Transactional
    public boolean deleteKnowledgeItem(Long id) {
        if (knowledgeItemRepository.existsById(id)) {
            knowledgeItemRepository.deleteById(id);
            // 注意：由于 @ElementCollection，关联的 tags 会自动被删除
            return true;
        }
        return false;
    }
}