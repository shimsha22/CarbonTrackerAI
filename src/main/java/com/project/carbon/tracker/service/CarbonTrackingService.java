package com.project.carbon.tracker.service;

import com.project.carbon.tracker.model.CarbonLog;
import com.project.carbon.tracker.model.User;
import com.project.carbon.tracker.repository.CarbonLogRepository;
import com.project.carbon.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarbonTrackingService {
    private final UserRepository userRepository;
    private final CarbonLogRepository carbonLogRepository;


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public CarbonLog addLogToUser(Long userId, CarbonLog log) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        log.setUser(existingUser);


        if (log.getDate() == null) {
            log.setDate(LocalDate.now());
        }

        double footprint = calculateFootprint(log.getActivityType(), log.getAmount());
        log.setCarbonFootprint(footprint);

        return carbonLogRepository.save(log);
    }
    private double calculateFootprint(String activityType, Double amount) {

        switch(activityType.toUpperCase()) {
            case "DRIVING":
                return amount * 0.41; // 0.41 kg CO2 per mile for an average gas car
            case "ELECTRICITY":
                return amount * 0.39; // 0.39 kg CO2 per kWh
            case "MEAT_MEAL":
                return amount * 6.6; // 6.6 kg CO2 per beef meal
            default:
                return 0.0; // Unrecognized activity
        }
    }

    public Map<String, Object> getUserDashboard(Long userId) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        List<CarbonLog> userLogs = carbonLogRepository.findByUserId(userId);


        double totalFootprint = userLogs.stream()
                .filter(log -> log.getCarbonFootprint() != null)
                .mapToDouble(CarbonLog::getCarbonFootprint)
                .sum();


        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("user", existingUser.getUsername());
        dashboardData.put("totalLogs", userLogs.size());
        dashboardData.put("totalCarbonFootprintKg", totalFootprint);
        dashboardData.put("activityHistory", userLogs);

        return dashboardData;
    }

    public User loginOrRegister(String username) {

        return userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(username.toLowerCase().replace(" ", "") + "@example.com");
            return userRepository.save(newUser);
        });
    }


    public void deleteLog(Long logId) {
        carbonLogRepository.deleteById(logId);
    }

}
