package com.example.SP26SE025.service;

import com.example.SP26SE025.dtos.DoctorRegistrationDto;
import com.example.SP26SE025.dtos.PatientListDto;
import com.example.SP26SE025.entity.*;
import com.example.SP26SE025.repository.ClinicProfileRepository;
import com.example.SP26SE025.repository.SubscriptionRepository;
import com.example.SP26SE025.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ClinicProfileRepository clinicProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================================
    // PHẦN 1: QUẢN LÝ BÁC SĨ
    // ==========================================================

    public List<User> getAllDoctors() {
        return userRepository.findByRole(Role.DOCTOR);
    }

    public void createDoctor(DoctorRegistrationDto dto) {
        User doctor = new User();
        doctor.setFullName(dto.getFullName());
        doctor.setUsername(dto.getUsername());
        doctor.setEmail(dto.getEmail());
        doctor.setSpecialist(dto.getSpecialist());
        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setRole(Role.DOCTOR);
        doctor.setEnabled(true);
        userRepository.save(doctor);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(user.getFullName());
            existingUser.setSpecialist(user.getSpecialist());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setEmail(user.getEmail());
            userRepository.save(existingUser);
        }
    }

    // ==========================================================
    // PHẦN 2: QUẢN LÝ BỆNH NHÂN
    // ==========================================================

    public List<PatientListDto> getAllPatients(String keyword) {

        List<User> patients = userRepository.findByRole(Role.CUSTOMER);
        List<PatientListDto> dtoList = new ArrayList<>();

        String searchKey = (keyword != null) ? keyword.toLowerCase().trim() : "";

        for (User user : patients) {

            if (!searchKey.isEmpty()) {
                String displayId = "bn-" + user.getId();
                String fullName = (user.getFullName() != null) ? user.getFullName().toLowerCase() : "";
                String phone = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : "";

                boolean matchId = displayId.contains(searchKey);
                boolean matchName = fullName.contains(searchKey);
                boolean matchPhone = phone.contains(searchKey);

                if (!matchId && !matchName && !matchPhone) {
                    continue;
                }
            }

            PatientListDto dto = new PatientListDto();
            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());

            String packageName = "Chưa đăng ký";
            String statusClass = "text-secondary";

            List<Subscription> subs =
                    subscriptionRepository.findByUserIdOrderByIdDesc(user.getId());

            if (!subs.isEmpty()) {

                List<String> names = new ArrayList<>();

                String firstPlanName = subs.get(0).getPlanName();
                if (firstPlanName != null) {
                    String lower = firstPlanName.toLowerCase();
                    if (lower.contains("vip")) {
                        statusClass = "text-warning fw-bold";
                    } else if (lower.contains("cơ bản")) {
                        statusClass = "text-primary fw-bold";
                    } else {
                        statusClass = "text-success fw-bold";
                    }
                }

                for (int i = 0; i < subs.size(); i++) {
                    if (i >= 3) {
                        names.add("...");
                        break;
                    }
                    if (subs.get(i).getPlanName() != null) {
                        names.add(subs.get(i).getPlanName());
                    }
                }

                if (!names.isEmpty()) {
                    packageName = String.join(", ", names);
                }
            }

            dto.setSubscriptionPlan(packageName);
            dto.setStatusClass(statusClass);

            dtoList.add(dto);
        }

        return dtoList;
    }

    // ==========================================================
    // PHẦN 3: ADMIN DUYỆT PHÒNG KHÁM
    // ==========================================================

    // Danh sách phòng khám chờ duyệt
    public List<ClinicProfile> getPendingClinics() {
        return clinicProfileRepository.findByVerificationStatus(
                VerificationStatus.PENDING
        );
    }

    // Duyệt phòng khám
    public void approveClinic(Long clinicProfileId) {

        ClinicProfile profile = clinicProfileRepository
                .findById(clinicProfileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng khám"));

        profile.setVerificationStatus(VerificationStatus.FULFILLED);
        clinicProfileRepository.save(profile);

        User user = userRepository
                .findByUsername(profile.getUsernameLink())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    // Từ chối phòng khám
    public void rejectClinic(Long clinicProfileId) {

        ClinicProfile profile = clinicProfileRepository
                .findById(clinicProfileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng khám"));

        profile.setVerificationStatus(VerificationStatus.REJECTED);
        clinicProfileRepository.save(profile);
    }
}
