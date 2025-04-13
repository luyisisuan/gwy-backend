package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.KnowledgeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeItemRepository extends JpaRepository<KnowledgeItem, Long> {

    // 按时间倒序获取所有条目
    List<KnowledgeItem> findAllByOrderByTimestampDesc();

    // 按分类筛选 (忽略大小写)，并按时间倒序
    List<KnowledgeItem> findByCategoryIgnoreCaseOrderByTimestampDesc(String category);

    /**
     * 搜索标题、内容或标签 (不区分大小写，但内容字段 content 除外)。
     * 注意：对 content 字段的搜索是区分大小写的，因为数据库的 LOWER() 函数通常不支持 LOB/TEXT 类型。
     * 如果需要对 content 进行不区分大小写的搜索，建议使用数据库特定的全文搜索功能。
     *
     * @param searchTerm 搜索关键词
     * @return 匹配的知识条目列表，按时间倒序排列
     */
    @Query("SELECT DISTINCT k FROM KnowledgeItem k LEFT JOIN k.tags t WHERE " +
            "LOWER(k.title) LIKE LOWER(concat('%', :searchTerm, '%')) OR " +
            // --- 修改点：移除了 k.content 外的 LOWER() ---
            "k.content LIKE concat('%', :searchTerm, '%') OR " +
            "LOWER(t) LIKE LOWER(concat('%', :searchTerm, '%')) " +
            "ORDER BY k.timestamp DESC")
    List<KnowledgeItem> searchByTermOrderByTimestampDesc(@Param("searchTerm") String searchTerm);


    /**
     * 按分类筛选并搜索标题、内容或标签 (不区分大小写，但内容字段 content 除外)。
     * 注意：对 content 字段的搜索是区分大小写的。
     *
     * @param category 分类名称
     * @param searchTerm 搜索关键词
     * @return 匹配的知识条目列表，按时间倒序排列
     */
    @Query("SELECT DISTINCT k FROM KnowledgeItem k LEFT JOIN k.tags t WHERE " +
            "k.category = :category AND (" + // 保持分类条件
            "LOWER(k.title) LIKE LOWER(concat('%', :searchTerm, '%')) OR " +
            // --- 修改点：移除了 k.content 外的 LOWER() ---
            "k.content LIKE concat('%', :searchTerm, '%') OR " +
            "LOWER(t) LIKE LOWER(concat('%', :searchTerm, '%'))" +
            ") ORDER BY k.timestamp DESC")
    List<KnowledgeItem> findByCategoryAndSearchTermOrderByTimestampDesc(
            @Param("category") String category,
            @Param("searchTerm") String searchTerm);
}