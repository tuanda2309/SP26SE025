package com.example.SP26SE025.service;

import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ======================================================
    // BASIC CRUD
    // ======================================================

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // ======================================================
    // FIND USER
    // ======================================================

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Dùng cho Spring Security (Principal.getName())
     * username = email
     */
    public User findByUsername(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    // ======================================================
    // CREATE / SAVE USER
    // ======================================================

    public User save(User user) {
        // Chỉ mã hoá password nếu chưa mã hoá
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // ======================================================
    // ⭐ UPDATE PROFILE – QUAN TRỌNG NHẤT
    // ======================================================

    /**
     * Update profile chuẩn Hibernate
     * → đảm bảo DateOfBirth LƯU DB
     */
    @Transactional
    public User updateProfile(Long userId, User newData) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ===== UPDATE CÁC TRƯỜNG PROFILE =====
        user.setFullName(newData.getFullName());
        user.setPhoneNumber(newData.getPhoneNumber());
        user.setDob(newData.getDob());                 // ⭐ FIX DOB
        user.setDiabetesType(newData.getDiabetesType());
        user.setHypertension(newData.getHypertension());
        user.setMedicalHistory(newData.getMedicalHistory());
        user.setAvatarPath(newData.getAvatarPath());
        user.setSpecialist(newData.getSpecialist());

        // Lưu vào database
        return userRepository.save(user);
    }
}