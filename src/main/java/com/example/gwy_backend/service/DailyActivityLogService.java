package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.DailyActivityLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map; // For stats

public interface DailyActivityLogService {

    /**
     * 增加指定日期的在线时长。如果当天记录不存在，则会创建。
     * @param date 日期
     * @param secondsToAdd 要增加的秒数 (应为正数)
     */
    void addOnlineDuration(LocalDate date, long secondsToAdd);

    /**
     * 获取指定日期的总在线时长（秒）。
     * @param date 日期
     * @return 当天的总在线秒数，如果当天无记录则返回 0
     */
    long getOnlineSecondsForDate(LocalDate date);

    /**
     * 获取今日的总在线时长（秒）。
     * @return 今日总在线秒数
     */
    long getTodayOnlineSeconds();

    /**
     * 获取指定日期范围内的每日在线时长记录。
     * @param startDate 开始日期 (包含)
     * @param endDate 结束日期 (包含)
     * @return DailyActivityLog 列表
     */
    List<DailyActivityLog> getLogsForDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取一些聚合统计数据 (例如：总计、本周、本月) - 优化方案
     * @return Map 包含 "total", "week", "month" 的秒数
     */
    Map<String, Long> getActivityStats();
}