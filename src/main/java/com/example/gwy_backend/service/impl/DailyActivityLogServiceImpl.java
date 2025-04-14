package com.example.gwy_backend.service.impl;

// 保持 DailyActivityLog 相关的导入，因为还需要处理在线时长
import com.example.gwy_backend.entity.DailyActivityLog;
import com.example.gwy_backend.repository.DailyActivityLogRepository;
// 导入 StudyLog 相关的 Repository
import com.example.gwy_backend.repository.StudyLogRepository; // <<< 导入 StudyLogRepository
import com.example.gwy_backend.service.DailyActivityLogService; // <<< 接口名可能需要调整
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime; // <<< 导入 LocalDateTime
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DailyActivityLogServiceImpl implements DailyActivityLogService { // <<< 类名和接口名可能需调整

    private static final Logger log = LoggerFactory.getLogger(DailyActivityLogServiceImpl.class);
    private final DailyActivityLogRepository activityLogRepository;
    private final StudyLogRepository studyLogRepository; // <<< 注入 StudyLogRepository

    @Autowired
    public DailyActivityLogServiceImpl(DailyActivityLogRepository activityLogRepository,
                                       StudyLogRepository studyLogRepository) { // <<< 修改构造函数
        this.activityLogRepository = activityLogRepository;
        this.studyLogRepository = studyLogRepository; // <<< 注入
    }

    // addOnlineDuration, getOnlineSecondsForDate, getTodayOnlineSeconds, getLogsForDateRange 方法保持不变
    // 因为它们仍然负责处理 daily_activity_log 表的在线时长数据
    @Override
    @Transactional
    public void addOnlineDuration(LocalDate date, long secondsToAdd) {
        if (secondsToAdd <= 0) {
            log.warn("Attempted to add non-positive duration ({}) for date {}", secondsToAdd, date);
            return;
        }
        log.debug("Adding {} seconds online duration for date {}", secondsToAdd, date);

        int updatedRows = activityLogRepository.incrementOnlineSeconds(date, secondsToAdd);

        if (updatedRows == 0) {
            log.info("No existing activity log found for date {}, creating new entry.", date);
            // **MODIFIED:** 创建新记录时，确保 logDate 也被设置
            // 使用接受 activityDate 的构造函数，它内部会设置 logDate
            DailyActivityLog newLog = new DailyActivityLog(date);
            // 或者手动设置:
            // DailyActivityLog newLog = new DailyActivityLog();
            // newLog.setActivityDate(date);
            // newLog.setLogDate(date); // <<< 确保 logDate 被设置

            newLog.setTotalOnlineSeconds(secondsToAdd);
            try {
                activityLogRepository.save(newLog);
            } catch (Exception e) {
                log.error("Error saving new activity log for date {} after increment failed.", date, e);
                // 可以考虑再次尝试 increment 或抛出异常
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getOnlineSecondsForDate(LocalDate date) {
        log.debug("Getting online seconds for date {}", date);
        return activityLogRepository.findByActivityDate(date)
                .map(DailyActivityLog::getTotalOnlineSeconds)
                .orElse(0L);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTodayOnlineSeconds() {
        return getOnlineSecondsForDate(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyActivityLog> getLogsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching activity logs from {} to {}", startDate, endDate);
        return activityLogRepository.findByActivityDateBetweenOrderByActivityDateDesc(startDate, endDate);
    }

    // **MODIFIED:** getActivityStats 现在计算 study_log 的时长
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getActivityStats() {
        log.info("Calculating activity stats based on StudyLog...");
        Map<String, Long> stats = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime nextDayStart = today.plusDays(1).atStartOfDay();

        // 计算总学习时长 (来自 study_log) - 需要在 StudyLogRepository 添加方法
        // long totalStudySeconds = studyLogRepository.getTotalStudyDurationSum(); // 假设有此方法
        // 暂时通过获取所有日志计算 (效率较低)
         long totalStudySeconds = studyLogRepository.findAll().stream().mapToLong(log -> log.getDurationSeconds() > 0 ? log.getDurationSeconds() : 0).sum();
        stats.put("total", totalStudySeconds);

        // 计算本周学习时长 (来自 study_log)
        LocalDate weekStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime weekStartDateTime = weekStartDate.atStartOfDay();
        long weekStudySeconds = studyLogRepository.sumDurationSecondsBetween(weekStartDateTime, nextDayStart);
        stats.put("week", weekStudySeconds);

        // 计算本月学习时长 (来自 study_log)
        LocalDate monthStartDate = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime monthStartDateTime = monthStartDate.atStartOfDay();
        long monthStudySeconds = studyLogRepository.sumDurationSecondsBetween(monthStartDateTime, nextDayStart);
        stats.put("month", monthStudySeconds);

        // 计算今日学习时长 (来自 study_log)
        long todayStudySeconds = studyLogRepository.sumDurationSecondsBetween(todayStart, nextDayStart);
        stats.put("today", todayStudySeconds); // key "today" 现在代表学习时长

        // 计算今日在线时长 (来自 daily_activity_log)
        long todayOnlineSec = getTodayOnlineSeconds(); // 调用内部方法
        stats.put("todayOnline", todayOnlineSec); // key "todayOnline" 代表在线时长

        log.info("Activity stats calculated: {}", stats);
        return stats;
    }

     // getAverageDailyStudyTime 方法也应该基于 study_log 计算
     @Override
     @Transactional(readOnly = true)
     public long getAverageDailyStudyTime(int days) {
          if (days <= 0) days = 30;
          log.info("Calculating average daily study time for the last {} days.", days);
          LocalDate endDate = LocalDate.now();
          LocalDate startDate = endDate.minusDays(days); // 从 N 天前开始

          // 调用 sumDurationSecondsBetween 计算范围内的总学习时长
          long totalSecondsInRange = studyLogRepository.sumDurationSecondsBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

          return (days > 0) ? totalSecondsInRange / days : 0;
     }

     // calculatePercentageChange 辅助方法保持不变
     // private double calculatePercentageChange(...) { ... }

}