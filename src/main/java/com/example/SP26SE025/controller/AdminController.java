package com.example.SP26SE025.controller;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import com.example.SP26SE025.security.CustomUserDetails;
import com.example.SP26SE025.security.JwtUtil;
import com.example.SP26SE025.service.CustomUserDetailsService;

@Controller
public class AdminController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/loginAdmin")
    public String loginPage() {
        return "login-view-admin";
    }

    @GetMapping("/admin/home")
    public String home() {
        return "admin/notifications";
    }
    @GetMapping("/admin/users2")
    public String users(Model model) {
        model.addAttribute("title", "Users Management");
        model.addAttribute("content", "admin/users :: content");
        model.addAttribute("pageCss", "/css/users.css");
        return "admin/users";
    }
    @GetMapping("/admin/clinics")
    public String clinics(Model model) {
        model.addAttribute("title", "Clinics Management");
        model.addAttribute("content", "admin/clinics :: content");
        model.addAttribute("pageCss", "/css/clinics.css");
        return "admin/clinics";
    }

    // ================= DOCTORS =================
    @GetMapping("/admin/doctors")
    public String doctors(Model model) {
        model.addAttribute("title", "Doctors Management");
        model.addAttribute("content", "admin/doctors :: content");
        model.addAttribute("pageCss", "/css/doctors.css");
        return "admin/doctors";
    }

    // ================= PACKAGES =================
    @GetMapping("/admin/packages")
    public String packages(Model model) {
        model.addAttribute("title", "Service Packages");
        model.addAttribute("content", "admin/packages :: content");
        model.addAttribute("pageCss", "/css/packages.css");
        return "admin/packages";
    }

    // ================= AI MANAGEMENT =================
    @GetMapping("/admin/ai")
    public String ai(Model model) {
        model.addAttribute("title", "AI Management");
        model.addAttribute("content", "admin/ai :: content");
        model.addAttribute("pageCss", "/css/ai.css");
        return "admin/ai-management";
    }

    // ================= REPORTS =================
    @GetMapping("/admin/reports")
    public String reports(Model model) {
        model.addAttribute("title", "Reports");
        model.addAttribute("content", "admin/reports :: content");
        model.addAttribute("pageCss", "/css/reports.css");
        return "admin/reports";
    }
    @GetMapping("/admin/notifications")
    public String notifications(Model model) {
        model.addAttribute("title", "Notifications");
        model.addAttribute("content", "admin/notifications :: content");
        model.addAttribute("pageCss", "/css/notifications.css");
        return "admin/notifications";
    }
 

    @PostMapping("/authenticateAdmin")
    public String authenticate(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(username);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "").trim().toUpperCase();

            return switch (role) {
                case "ADMIN" -> "redirect:/admin/home";
                // case "CLINIC" -> "redirect:/clinic/home";
                // case "DOCTOR" -> "redirect:/doctor/dashboard";
                default -> "redirect:/loginAdmin?error=true";
            };
        } catch (Exception e) {
            e.printStackTrace(); // Thêm dòng này để in lỗi ra console
            return "redirect:/loginAdmin?error=true";
        }
    }
}
