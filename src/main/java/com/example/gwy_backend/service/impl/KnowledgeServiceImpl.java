package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.KnowledgeItem;
import com.example.gwy_backend.repository.KnowledgeItemRepository;
import com.example.gwy_backend.service.KnowledgeService;
// 不再需要 EntityManager, PersistenceContext, TypedQuery 的导入了，如果你之前加了，可以删掉
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger; // 保留 Logger 导入
import org.slf4j.LoggerFactory; // 保留 LoggerFactory 导入

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);

    private final KnowledgeItemRepository knowledgeItemRepository;

    // 不再需要 EntityManager 注入了，如果你之前加了，可以删掉
    // @PersistenceContext
    // private EntityManager entityManager;

    @Autowired
    public KnowledgeServiceImpl(KnowledgeItemRepository knowledgeItemRepository) {
        this.knowledgeItemRepository = knowledgeItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm) {
        boolean hasCategory = StringUtils.hasText(category) && !"all".equalsIgnoreCase(category);
        boolean hasSearchTerm = StringUtils.hasText(searchTerm);

        if (hasCategory && hasSearchTerm) {
            // 同时有分类和搜索词 (调用安全的方法)
            log.info("Searching by category '{}' and term '{}' using safe repository method", category, searchTerm);
            return knowledgeItemRepository.findByCategoryAndSearchTermOrderByTimestampDesc(category, searchTerm);
        } else if (hasCategory) {
            // 只有分类 (调用安全的方法)
            log.info("Searching by category '{}' using safe repository method", category);
            return knowledgeItemRepository.findByCategoryIgnoreCaseOrderByTimestampDesc(category);
        } else if (hasSearchTerm) {
            // --- 只有搜索词 - 已恢复使用安全的方法 ---
            log.info("Searching by term '{}' using safe repository method", searchTerm); // 日志级别恢复为 INFO

            // --- 恢复调用 Repository 的安全方法 ---
            return knowledgeItemRepository.searchByTermOrderByTimestampDesc(searchTerm);
            // --- 恢复结束 ---

        } else {
            // 没有筛选条件，获取所有 (调用安全的方法)
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
        knowledgeItem.setId(null); // 确保是新增
        if (knowledgeItem.getTags() == null) {
            knowledgeItem.setTags(List.of());
        }
        return knowledgeItemRepository.save(knowledgeItem);
    }

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









// 测试代码

// package com.example.gwy_backend.service.impl;

// import com.example.gwy_backend.entity.KnowledgeItem;
// import com.example.gwy_backend.repository.KnowledgeItemRepository;
// import com.example.gwy_backend.service.KnowledgeService;
// import jakarta.persistence.EntityManager; // <<<--- 1. 导入 EntityManager
// import jakarta.persistence.PersistenceContext; // <<<--- 2. 导入 PersistenceContext
// import jakarta.persistence.TypedQuery; // <<<--- 3. 导入 TypedQuery
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.util.StringUtils;

// import java.util.List;
// import java.util.Optional;
// import org.slf4j.Logger; // <<<--- (可选) 导入 Logger
// import org.slf4j.LoggerFactory; // <<<--- (可选) 导入 LoggerFactory

// @Service
// public class KnowledgeServiceImpl implements KnowledgeService {

//     private static final Logger log = LoggerFactory.getLogger(KnowledgeServiceImpl.class);
//     private final KnowledgeItemRepository knowledgeItemRepository;
//     @PersistenceContext
//     private EntityManager entityManager;

//     @Autowired
//     public KnowledgeServiceImpl(KnowledgeItemRepository knowledgeItemRepository) {
//         this.knowledgeItemRepository = knowledgeItemRepository;
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<KnowledgeItem> getKnowledgeItems(String category, String searchTerm) {
//         boolean hasCategory = StringUtils.hasText(category) && !"all".equalsIgnoreCase(category);
//         boolean hasSearchTerm = StringUtils.hasText(searchTerm);

//         if (hasCategory && hasSearchTerm) {
//             log.info("Searching by category '{}' and term '{}'", category, searchTerm);
//             return knowledgeItemRepository.findByCategoryAndSearchTermOrderByTimestampDesc(category, searchTerm);
//         } else if (hasCategory) {
//             log.info("Searching by category '{}'", category);
//             return knowledgeItemRepository.findByCategoryIgnoreCaseOrderByTimestampDesc(category);
//         } else if (hasSearchTerm) {
//             log.warn("Executing potentially VULNERABLE search for term: '{}'", searchTerm);

//             // --- 修改：构造易受攻击的原生 SQL 字符串 ---
//             // 警告：这是故意制造的 SQL 注入漏洞，仅用于演示
//             String vulnerableSql = "SELECT DISTINCT k.* FROM knowledge_item k LEFT JOIN knowledge_item_tags t ON k.id = t.item_id WHERE " +
//                                    "LOWER(k.title) LIKE LOWER('%" + searchTerm + "%') OR " + // 直接拼接 searchTerm
//                                    "k.content LIKE '%" + searchTerm + "%' OR " +            // 直接拼接 searchTerm
//                                    "LOWER(t.tag) LIKE LOWER('%" + searchTerm + "%') " +      // 直接拼接 searchTerm
//                                    "ORDER BY k.timestamp DESC";
//             // 注意：我们这里不再使用 replace("'", "''")，让注入更容易成功

//             log.warn("Executing Vulnerable Native SQL: {}", vulnerableSql); // 日志区分 Native SQL

//             try {
//                 // --- 修改：使用 createNativeQuery 执行原生 SQL ---
//                 // 我们需要告诉 JPA 返回的结果应该映射到哪个实体类
//                 @SuppressWarnings("unchecked") // 因为 createNativeQuery 返回原始列表，需要类型转换
//                 List<KnowledgeItem> resultList = entityManager.createNativeQuery(vulnerableSql, KnowledgeItem.class)
//                                                               .getResultList();
//                 return resultList;
//             } catch (Exception e) {
//                 log.error("Error executing vulnerable Native SQL query for search term '{}': {}", searchTerm, e.getMessage(), e); // 包含异常堆栈
//                 return List.of();
//             }
//             // --- 修改结束 ---

//         } else {
//             log.info("Fetching all knowledge items");
//             return knowledgeItemRepository.findAllByOrderByTimestampDesc();
//         }
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public Optional<KnowledgeItem> getKnowledgeItemById(Long id) {
//         log.info("Fetching knowledge item by id: {}", id);
//         return knowledgeItemRepository.findById(id);
//     }

//     @Override
//     @Transactional
//     public KnowledgeItem addKnowledgeItem(KnowledgeItem knowledgeItem) {
//         log.info("Adding new knowledge item with title: {}", knowledgeItem.getTitle());
//         knowledgeItem.setId(null); // 确保是新增
//         if (knowledgeItem.getTags() == null) {
//             knowledgeItem.setTags(List.of());
//         }
//         return knowledgeItemRepository.save(knowledgeItem);
//     }

//     @Override
//     @Transactional
//     public boolean deleteKnowledgeItem(Long id) {
//         log.info("Attempting to delete knowledge item with id: {}", id);
//         if (knowledgeItemRepository.existsById(id)) {
//             knowledgeItemRepository.deleteById(id);
//             log.info("Successfully deleted knowledge item with id: {}", id);
//             return true;
//         }
//         log.warn("Knowledge item with id: {} not found for deletion", id);
//         return false;
//     }
// }