package com.example.SP26SE025.controller;

import com.example.SP26SE025.dtos.NotificationDTO;
import com.example.SP26SE025.dtos.UserProfileDTO;
import com.example.SP26SE025.entity.Notification;
import com.example.SP26SE025.entity.ServicePackage;
import com.example.SP26SE025.entity.Subscription;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.entity.Role; 
import com.example.SP26SE025.repository.ServicePackageRepository;
import com.example.SP26SE025.repository.SubscriptionRepository;
import com.example.SP26SE025.service.NotificationService;
import com.example.SP26SE025.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ServicePackageRepository packageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // --- HÀM LẤY USER AN TOÀN (TỰ TẠO NẾU CHƯA CÓ) ---
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String email;
        String name = "Người dùng mới";
        String avatar = null;

        if (auth.getPrincipal() instanceof OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            avatar = oauth2User.getAttribute("picture");
        } else {
            email = auth.getName();
        }

        if (email == null) return null;

        User user = userService.findByEmail(email);

        // NẾU USER CHƯA TỒN TẠI -> TẠO MỚI VÀ GÁN GIÁ TRỊ MẶC ĐỊNH
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName(name != null ? name : "Khách hàng");
            user.setAvatarPath(avatar);
            user.setCreatedAt(LocalDateTime.now());
            
            // --- QUAN TRỌNG: GÁN MẶC ĐỊNH TRÁNH LỖI NULL ---
            user.setPhoneNumber(""); 
            user.setDiabetesType("NONE"); 
            user.setHypertension(false);
            user.setMedicalHistory("");
            
            // Set Role mặc định (Sửa lại CUSTOMER nếu enum bạn tên khác)
            user.setRole(Role.CUSTOMER); 
            
            userService.save(user);
        }

        return user;
    }

    // --- 1. DASHBOARD ---
    @GetMapping("/customer/home")
    public String customerHome(Model model) {
        User user = getAuthenticatedUser();
        model.addAttribute("currentUser", user);
        return "customer/home";
    }

    // --- 2. PROFILE (CHỖ ĐANG BỊ LỖI) ---
    @GetMapping("/customer/profile")
    public String showProfile(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) return "redirect:/login";

        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        
        // Xử lý null khi hiển thị ra form
        dto.setPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        dto.setAvatarPath(user.getAvatarPath());
        
        // Tránh lỗi null ngày sinh
        if (user.getDob() != null) {
            dto.setDob(user.getDob());
        }
        
        dto.setDiabetesType(user.getDiabetesType());
        dto.setHypertension(user.getHypertension());
        dto.setMedicalHistory(user.getMedicalHistory());

        model.addAttribute("userProfile", dto);
        return "customer/profile";
    }

    // --- UPDATE PROFILE ---
    // @PostMapping("/customer/profile/update")
    // public String updateProfile(
    //         @ModelAttribute("userProfile") UserProfileDTO userProfile,
    //         @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    // ) throws IOException {

    //     User user = getAuthenticatedUser();
    //     if (user == null) return "redirect:/login";

    //     if (avatarFile != null && !avatarFile.isEmpty()) {
    //         String base64 = Base64.getEncoder().encodeToString(avatarFile.getBytes());
    //         user.setAvatarPath("data:" + avatarFile.getContentType() + ";base64," + base64);
    //     }

    //     user.setFullName(userProfile.getFullName());
    //     user.setPhoneNumber(userProfile.getPhone());
    //     user.setDob(userProfile.getDob());
    //     user.setDiabetesType(userProfile.getDiabetesType());
    //     user.setHypertension(userProfile.getHypertension());
    //     user.setMedicalHistory(userProfile.getMedicalHistory());

    //     userService.save(user);

    //     return "redirect:/customer/profile?success";
    // }

    @PostMapping("/customer/profile/update")
    public String updateProfile(
            @ModelAttribute("userProfile") UserProfileDTO userProfile,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile
    ) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());

        if (user == null) {
            return "redirect:/customer/profile?error";
        }

        // --- XỬ LÝ AVATAR BASE64 ---
        if (avatarFile != null && !avatarFile.isEmpty()) {
            byte[] bytes = avatarFile.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String mimeType = avatarFile.getContentType();

            String dataUrl = "data:" + mimeType + ";base64," + base64;
            user.setAvatarPath(dataUrl);
        }

        // --- UPDATE CÁC TRƯỜNG PROFILE ---
        user.setFullName(userProfile.getFullName());
        user.setPhoneNumber(userProfile.getPhone());
        user.setDob(userProfile.getDob());
        user.setDiabetesType(userProfile.getDiabetesType());
        user.setHypertension(userProfile.getHypertension());
        user.setMedicalHistory(userProfile.getMedicalHistory());

        userService.save(user);

        return "redirect:/customer/profile?success";
    }


    @GetMapping("/customer/profile/update")
    public String redirectUpdate() {
        return "redirect:/customer/profile";
    }

    // --- 3. NOTIFICATIONS ---
    @GetMapping("/customer/notifications")
    public String showNotifications(Model model) {
        User user = getAuthenticatedUser();
        if (user != null) {
            List<Notification> notifs = notificationService.getAllNotifications(user);
            List<NotificationDTO> dtos = notifs.stream()
                    .map(n -> new NotificationDTO(n.getTitle(), n.getMessage(), n.getType(), n.getCreatedAt(), n.isRead()))
                    .collect(Collectors.toList());
            model.addAttribute("notifications", dtos);
        } else {
            model.addAttribute("notifications", new ArrayList<>());
        }
        return "customer/notifications";
    }

    // --- 4. PACKAGES ---
    @GetMapping("/customer/packages")
    public String showPackages(Model model) {
        model.addAttribute("packages", packageRepository.findByIsActiveTrue());
        return "customer/packages";
    }

    @GetMapping("/customer/packages/buy")
    public String buyPackage(@RequestParam("planId") Long planId) {
        User user = getAuthenticatedUser();
        ServicePackage pkg = packageRepository.findById(planId).orElse(null);

        if (user != null && pkg != null) {
            Subscription sub = new Subscription();
            sub.setUser(user);
            sub.setPlanName(pkg.getPackageName());
            sub.setPrice(pkg.getPrice());
            sub.setStartDate(LocalDateTime.now());
            sub.setStatus("ACTIVE");
            
            if (pkg.getPeriod() != null && pkg.getPeriod().toLowerCase().contains("năm")) {
                sub.setEndDate(LocalDateTime.now().plusYears(1));
            } else {
                sub.setEndDate(LocalDateTime.now().plusMonths(1));
            }
            subscriptionRepository.save(sub);
        }
        return "redirect:/customer/home?success=bought";
    }

    // --- 5. PLACEHOLDERS ---
    // @GetMapping("/customer/reports/analysis")
    // public String showAnalysis(Model model) {
    //     model.addAttribute("historyList", new ArrayList<>());
    //     return "customer/analysis_report";
    // }

    @GetMapping({"/customer/upload", "/customer/doctor-chat"})
    public String redirectTemp() {
        return "redirect:/customer/home";
    }
}
