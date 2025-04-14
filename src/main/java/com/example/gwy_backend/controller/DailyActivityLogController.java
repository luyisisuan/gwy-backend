package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.DailyActivityLog; // 确保导入实体
import com.example.gwy_backend.service.DailyActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List; // 确保导入 List
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class DailyActivityLogController {

    private static final Logger log = LoggerFactory.getLogger(DailyActivityLogController.class);
    private final DailyActivityLogService activityLogService;

    @Autowired
    public DailyActivityLogController(DailyActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * 心跳或增加今日在线时长的端点。
     */
    @PostMapping("/ping")
    public ResponseEntity<?> recordActivity(@RequestBody Map<String, Long> payload) {
        Long duration = payload.get("durationSeconds");
        if (duration == null || duration <= 0) {
            log.warn("Received ping request with invalid duration: {}", duration);
            return ResponseEntity.badRequest().body("Invalid durationSeconds.");
        }
        LocalDate today = LocalDate.now();
        log.debug("Received activity ping for today ({}), adding {} seconds.", today, duration);
        try {
            activityLogService.addOnlineDuration(today, duration);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error recording activity duration for date {}", today, e);
            return ResponseEntity.internalServerError().body("Error recording activity.");
        }
    }

    /**
     * 获取今日总在线时长 (秒)。
     */
    @GetMapping("/today")
    public ResponseEntity<Map<String, Long>> getTodayActivity() {
        log.debug("Received request for today's activity duration.");
        long seconds = activityLogService.getTodayOnlineSeconds();
        return ResponseEntity.ok(Map.of("todaySeconds", seconds));
    }

    /**
     * 获取聚合的统计数据 (总计、本周、本月、今日在线时长)。
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getActivityStats() {
         log.info("Received request for activity stats.");
         // 注意：这里的 stats 是基于 DailyActivityLog (在线时长) 计算的
         Map<String, Long> stats = activityLogService.getActivityStats();
         return ResponseEntity.ok(stats);
    }

    /**
     * 获取指定日期的在线时长 (秒)。
     */
    @GetMapping("/date/{dateString}")
    public ResponseEntity<?> getActivityForDate(@PathVariable String dateString) {
        log.debug("Received request for activity duration on date: {}", dateString);
        try {
            LocalDate date = LocalDate.parse(dateString);
            long seconds = activityLogService.getOnlineSecondsForDate(date);
            return ResponseEntity.ok(Map.of("date", dateString, "seconds", seconds));
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format received: {}", dateString);
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD.");
        } catch (Exception e) {
             log.error("Error getting activity for date {}", dateString, e);
             return ResponseEntity.internalServerError().body("Error getting activity data.");
        }
    }

     /**
      * (可选) 获取指定日期范围的每日日志
      */
      @GetMapping("/range")
      public ResponseEntity<?> getActivityForDateRange(
              @RequestParam("startDate") String startDateString,
              @RequestParam("endDate") String endDateString) {
          log.debug("Received request for activity log range: {} to {}", startDateString, endDateString);
          try {
              LocalDate startDate = LocalDate.parse(startDateString);
              LocalDate endDate = LocalDate.parse(endDateString);
              if (startDate.isAfter(endDate)) {
                   return ResponseEntity.badRequest().body("Start date cannot be after end date.");
              }
              List<DailyActivityLog> logs = activityLogService.getLogsForDateRange(startDate, endDate);
              return ResponseEntity.ok(logs);
          } catch (DateTimeParseException e) {
               log.warn("Invalid date format received for range: {} or {}", startDateString, endDateString);
               return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD.");
          } catch (Exception e) {
               log.error("Error getting activity logs for range {} to {}", startDateString, endDateString, e);
               return ResponseEntity.internalServerError().body("Error getting activity logs.");
          }
      }
}