package com.example.SP26SE025.controller;

import com.example.SP26SE025.entity.ClinicProfile;
import com.example.SP26SE025.entity.VerificationStatus;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.ClinicRepository;
import com.example.SP26SE025.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/clinics")
public class AdminClinicController {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔹 Lấy tất cả phòng khám
    @GetMapping
    public List<ClinicProfile> getAll() {
        return clinicRepository.findAll();
    }

    // 🔹 DUYỆT PHÒNG KHÁM
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {

        // 1. Lấy clinic
        ClinicProfile clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        // 2. Đổi trạng thái clinic
        clinic.setVerificationStatus(VerificationStatus.FULFILLED);
        clinicRepository.save(clinic);

        // 3. BẬT USER ĐƯỢC LOGIN
        User user = userRepository.findByUsername(clinic.getUsernameLink())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true); // ⭐ CHO PHÉP LOGIN
        userRepository.save(user);

        return ResponseEntity.ok("Clinic approved & user enabled");
    }

    // 🔹 TỪ CHỐI / TẠM DỪNG
    @PutMapping("/{id}/suspend")
    public ResponseEntity<?> suspend(@PathVariable Long id) {

        ClinicProfile clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        clinic.setVerificationStatus(VerificationStatus.REJECTED);
        clinicRepository.save(clinic);

        // ❌ KHÔNG bật user
        return ResponseEntity.ok("Clinic suspended");
    }
}
