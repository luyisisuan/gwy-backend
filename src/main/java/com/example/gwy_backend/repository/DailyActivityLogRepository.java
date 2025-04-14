package com.example.gwy_backend.repository;

import com.example.gwy_backend.entity.DailyActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional; // 确保导入
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DailyActivityLogRepository extends JpaRepository<DailyActivityLog, Long> {

    // 根据 activityDate 查找记录 (保持不变)
    Optional<DailyActivityLog> findByActivityDate(LocalDate activityDate);

    // 增加指定 activityDate 的在线时长 (保持不变)
    @Modifying
    @Transactional
    @Query("UPDATE DailyActivityLog d SET d.totalOnlineSeconds = d.totalOnlineSeconds + :secondsToAdd WHERE d.activityDate = :date")
    int incrementOnlineSeconds(@Param("date") LocalDate date, @Param("secondsToAdd") long secondsToAdd);

    // 获取指定日期范围内的记录 (保持不变, 仍按 activityDate 查)
    List<DailyActivityLog> findByActivityDateBetweenOrderByActivityDateDesc(LocalDate startDate, LocalDate endDate);

    // 获取总的在线时长（所有记录）(保持不变)
    @Query("SELECT COALESCE(SUM(d.totalOnlineSeconds), 0) FROM DailyActivityLog d")
    long getTotalOnlineSecondsSum();
}