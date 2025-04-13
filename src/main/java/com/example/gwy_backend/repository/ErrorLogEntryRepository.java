package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.ErrorLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 用于自定义 JPQL 查询
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ErrorLogEntryRepository extends JpaRepository<ErrorLogEntry, Long> {

    // Spring Data JPA 会根据方法名自动生成查询
    // 例如，按模块筛选 (忽略大小写)
    List<ErrorLogEntry> findBySubjectIgnoreCase(String subject);

    // 如果需要按时间倒序获取所有条目
    List<ErrorLogEntry> findAllByOrderByTimestampDesc();

    // 也可以使用 JPQL 自定义查询
    // @Query("SELECT e FROM ErrorLogEntry e WHERE e.subject = :subject ORDER BY e.timestamp DESC")
    // List<ErrorLogEntry> findBySubjectSorted(@Param("subject") String subject);
}