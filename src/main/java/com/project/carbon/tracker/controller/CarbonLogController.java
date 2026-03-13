package com.project.carbon.tracker.controller;


import com.project.carbon.tracker.model.CarbonLog;
import com.project.carbon.tracker.service.CarbonTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CarbonLogController {
    private final CarbonTrackingService carbonTrackingService;


    @PostMapping("/user/{userId}")
    public ResponseEntity<CarbonLog> addLogToUser(
            @PathVariable Long userId,
            @RequestBody CarbonLog log) {

        CarbonLog savedLog = carbonTrackingService.addLogToUser(userId, log);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDashboard(@PathVariable Long userId) {
        Map<String, Object> dashboard = carbonTrackingService.getUserDashboard(userId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long logId) {
        carbonTrackingService.deleteLog(logId);
        return ResponseEntity.noContent().build();
    }
}
