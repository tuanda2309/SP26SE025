package com.example.SP26SE025.controller;

import com.example.SP26SE025.dtos.DoctorRegistrationDto;
import com.example.SP26SE025.entity.ClinicProfile;
import com.example.SP26SE025.entity.ServicePackage; // [QUAN TRỌNG] Nhớ import Entity này
import com.example.SP26SE025.entity.Subscription;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.ServicePackageRepository;
import com.example.SP26SE025.repository.SubscriptionRepository;
import com.example.SP26SE025.service.ClinicAdminService;
import com.example.SP26SE025.service.ClinicSettingService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; 
import java.util.List;

@Controller
@RequestMapping("/clinic")
public class ClinicController {

    @Autowired
    private ClinicAdminService clinicAdminService;

    @Autowired
    private ClinicSettingService clinicSettingService;

    @Autowired
    private ServicePackageRepository packageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // --- CÁC TRANG CƠ BẢN ---
    @GetMapping("/home")
    public String home() {
        return "clinic/home";
    }

    @GetMapping("/upload")
    public String upload() {
        return "clinic/upload";
    }

    @GetMapping("/reports/patient")
    public String showPatientReports() {
        return "clinic/report_tracking";
    }

    // @GetMapping("/reports/summary")
    // public String showStatistics() {
    //     return "clinic/statistics";
    // }

    // ========================================================================
    // QUẢN LÝ TÀI KHOẢN (BÁC SĨ / BỆNH NHÂN)
    // ========================================================================
    
    @GetMapping("/admin/users")
    public String userManagement(Model model, 
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "tab", defaultValue = "doctors") String activeTab) {
        
        if (keyword != null && !keyword.isEmpty()) {
            activeTab = "patients";
        }

        model.addAttribute("doctorsList", clinicAdminService.getAllDoctors());
        model.addAttribute("patientsList", clinicAdminService.getAllPatients(keyword));
        model.addAttribute("newDoctor", new DoctorRegistrationDto());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", activeTab);

        return "clinic/user_management";
    }

    @PostMapping("/admin/users/add-doctor")
    public String addDoctor(@ModelAttribute("newDoctor") DoctorRegistrationDto doctorDto) {
        clinicAdminService.createDoctor(doctorDto);
        return "redirect:/clinic/admin/users?success";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        clinicAdminService.deleteUser(id);
        return "redirect:/clinic/admin/users?deleted";
    }

    @PostMapping("/admin/users/update")
    public String updateDoctor(@ModelAttribute User user) {
        clinicAdminService.updateUser(user);
        return "redirect:/clinic/admin/users?updated";
    }

    // ========================================================================
    // QUẢN LÝ GÓI DỊCH VỤ & THỐNG KÊ (ĐÃ SỬA LỖI 404)
    // ========================================================================
    
    @GetMapping("/subscription")
    public String showSubscriptionPage(Model model) {
        // 1. [MỚI] Lấy danh sách gói dịch vụ để hiển thị 3 cột trên cùng
        model.addAttribute("packages", packageRepository.findAll());

        // 2. Lấy danh sách người đăng ký
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        model.addAttribute("subscriptions", subscriptions);

        // 3. Tính toán thống kê
        long totalSubscribers = subscriptions.size();
        double totalRevenue = subscriptions.stream()
                .mapToDouble(sub -> sub.getPrice() != null ? sub.getPrice() : 0.0)
                .sum();
        long expiringSoonCount = subscriptions.stream()
                .filter(sub -> "EXPIRING_SOON".equals(sub.getStatus())) 
                .count();

        model.addAttribute("totalSubscribers", totalSubscribers);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("expiringSoonCount", expiringSoonCount);

        return "clinic/subscription";
    }

    // --- [FIX 404] HÀM XỬ LÝ LƯU GÓI DỊCH VỤ ---
    @PostMapping("/subscription/update")
    public String updatePackage(@RequestParam(required = false) Long id, @ModelAttribute ServicePackage pkg) {
        
        if (id != null) {
            // --- TRƯỜNG HỢP SỬA (UPDATE) ---
            ServicePackage existingPkg = packageRepository.findById(id).orElse(null);
            if (existingPkg != null) {
                existingPkg.setPackageName(pkg.getPackageName());
                existingPkg.setPrice(pkg.getPrice());
                existingPkg.setPeriod(pkg.getPeriod());
                existingPkg.setFeatures(pkg.getFeatures());
                existingPkg.setPopular(pkg.isPopular());
                // existingPkg.setDescription(pkg.getDescription()); 
                
                packageRepository.save(existingPkg);
            }
        } else {
            // --- TRƯỜNG HỢP TẠO MỚI (CREATE) ---
            pkg.setActive(true); // Mặc định gói mới sẽ Active
            packageRepository.save(pkg);
        }
        
        return "redirect:/clinic/subscription?success=saved";
    }

    // --- HÀM XỬ LÝ ẨN/HIỆN GÓI (Cho nút Ngừng cung cấp) ---
    @GetMapping("/subscription/toggle/{id}")
    public String togglePackage(@PathVariable Long id) {
        ServicePackage pkg = packageRepository.findById(id).orElse(null);
        if (pkg != null) {
            pkg.setActive(!pkg.isActive());
            packageRepository.save(pkg);
        }
        return "redirect:/clinic/subscription?success=toggled";
    }

    // --- GIẢ LẬP MUA (Giữ nguyên) ---
    @GetMapping("/subscription/purchase")
    public String initiatePurchase(@RequestParam String plan) {
        System.out.println("Người dùng muốn mua gói: " + plan);
        return "redirect:/clinic/subscription?success=true";
    }

    // ========================================================================
    // [FR-22] THIẾT LẬP PHÒNG KHÁM (SETTINGS)
    // ========================================================================

    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ClinicProfile profile = clinicSettingService.getProfile(auth.getName());
        model.addAttribute("clinicProfile", profile);
        return "clinic/clinic_settings";
    }

    @PostMapping("/settings/update-info")
    public String updateGeneralInfo(@ModelAttribute ClinicProfile clinicProfile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        clinicSettingService.updateGeneralInfo(auth.getName(), clinicProfile);
        return "redirect:/clinic/settings?success=info";
    }

    @PostMapping("/settings/verify")
    public String uploadVerification(
            @RequestParam("taxId") String taxId,
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            clinicSettingService.uploadVerificationDocs(auth.getName(), taxId, file1, file2);
            return "redirect:/clinic/settings?success=verify";
        } catch (IOException e) {
            return "redirect:/clinic/settings?error=upload";
        }
    }

    @PostMapping("/settings/password")
    public String changePassword(
            @RequestParam("currentPass") String currentPass,
            @RequestParam("newPass") String newPass) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean success = clinicSettingService.changePassword(auth.getName(), currentPass, newPass);
        
        if (success) {
            return "redirect:/clinic/settings?success=password";
        } else {
            return "redirect:/clinic/settings?error=password";
        }
    }
}