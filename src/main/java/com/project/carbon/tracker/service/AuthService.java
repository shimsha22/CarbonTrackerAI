package com.project.carbon.tracker.service;

import com.project.carbon.tracker.model.User;
import com.project.carbon.tracker.repository.UserRepository;
import com.project.carbon.tracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //Handle Registration Logic
    public Map<String, String> registerUser(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        // We hash the password BEFORE saving to the database
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(newUser);

        // Generate token for auto-login after registration
        String token = jwtUtil.generateToken(username);
        return createAuthResponse(token, newUser.getId());
    }

    // Handle Login Logic
    public Map<String, String> loginUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if the provided password matches the hashed password in the DB
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        String token = jwtUtil.generateToken(username);
        return createAuthResponse(token, user.getId());
    }

    // Helper to format the response
    private Map<String, String> createAuthResponse(String token, Long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", String.valueOf(userId));
        return response;
    }
}
