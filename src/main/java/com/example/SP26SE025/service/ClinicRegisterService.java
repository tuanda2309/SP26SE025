package com.example.SP26SE025.service;

import com.example.SP26SE025.dtos.ClinicRegisterDTO;
import com.example.SP26SE025.entity.*;
import com.example.SP26SE025.repository.ClinicProfileRepository;
import com.example.SP26SE025.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClinicRegisterService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClinicProfileRepository clinicProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ⭐ TRẢ VỀ RegisterResult (KHÔNG VOID)
    public RegisterResult registerClinic(ClinicRegisterDTO dto) {

        // 1. Check trùng
        if (userRepository.existsByUsername(dto.getUsername())) {
            return RegisterResult.USERNAME_EXISTS;
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return RegisterResult.EMAIL_EXISTS;
        }

        if (userRepository.existsByPhoneNumber(dto.getPhone())) {
            return RegisterResult.PHONE_EXISTS;
        }

        // 2. Tạo USER (CHƯA ĐƯỢC LOGIN)
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setRole(Role.CLINIC);
        user.setEnabled(false); // ⭐ CHỜ ADMIN DUYỆT

        userRepository.save(user);

        // 3. Tạo CLINIC PROFILE
        ClinicProfile profile = new ClinicProfile();
        profile.setUsernameLink(dto.getUsername());
        profile.setClinicName("Phòng khám mới");
        profile.setVerificationStatus(VerificationStatus.PENDING);

        clinicProfileRepository.save(profile);

        return RegisterResult.SUCCESS;
    }
}
