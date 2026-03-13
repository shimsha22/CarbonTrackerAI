package com.project.carbon.tracker.controller;


import com.project.carbon.tracker.model.User;
import com.project.carbon.tracker.service.CarbonTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final CarbonTrackingService carbonTrackingService;


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = carbonTrackingService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username) {
        User user = carbonTrackingService.loginOrRegister(username);
        return ResponseEntity.ok(user);
    }

}
