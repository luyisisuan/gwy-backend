package com.example.gwy_backend.service.impl;

import com.example.gwy_backend.entity.DailyActivityLog;
import com.example.gwy_backend.repository.DailyActivityLogRepository;
import com.example.gwy_backend.service.DailyActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 确保导入

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // 需要导入 Optional

@Service
public class DailyActivityLogServiceImpl implements DailyActivityLogService {

    private static final Logger log = LoggerFactory.getLogger(DailyActivityLogServiceImpl.class);
    private final DailyActivityLogRepository activityLogRepository;

    @Autowired
    public DailyActivityLogServiceImpl(DailyActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

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

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getActivityStats() {
        log.info("Calculating activity stats...");
        Map<String, Long> stats = new HashMap<>();

        long totalSeconds = activityLogRepository.getTotalOnlineSecondsSum();
        stats.put("total", totalSeconds);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long weekSeconds = activityLogRepository.findByActivityDateBetweenOrderByActivityDateDesc(weekStart, today)
                .stream().mapToLong(DailyActivityLog::getTotalOnlineSeconds).sum();
        stats.put("week", weekSeconds);

        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
        long monthSeconds = activityLogRepository.findByActivityDateBetweenOrderByActivityDateDesc(monthStart, today)
                .stream().mapToLong(DailyActivityLog::getTotalOnlineSeconds).sum();
        stats.put("month", monthSeconds);

        // **MODIFIED:** getActivityStats 返回的 today 应该是 daily_activity_log 的今日在线时长
        // 而不是 study_log 的时长。如果需要 study_log 的今日时长，需要单独计算或从 StudyLogRepository 获取。
        // stats.put("today", getTodayOnlineSeconds()); // 正确，获取的是 DailyActivityLog 的今日时长
        // 如果后端 /stats API 设计为同时返回两种今日时长，需要调整：
        long todayOnlineSec = getTodayOnlineSeconds();
        stats.put("todayOnline", todayOnlineSec); // 返回今日在线时长
        // 假设还需要返回今日来自 study_log 的时长，需要注入 StudyLogRepository 并查询
        // 例如: long todayStudyLogSeconds = studyLogRepository.sumDurationSecondsBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        // stats.put("todayStudy", todayStudyLogSeconds);

        // 暂时只返回 DailyActivityLog 相关统计
        stats.put("today", todayOnlineSec); // 让 today 也等于今日在线时长


        log.info("Activity stats calculated: {}", stats);
        return stats;
    }
}