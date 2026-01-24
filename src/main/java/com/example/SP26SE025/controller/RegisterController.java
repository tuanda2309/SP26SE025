package com.example.SP26SE025.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.SP26SE025.entity.Role;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.UserRepository;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";  // Đã đổi thành "register"
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Vui lòng kiểm tra lại thông tin nhập!");
            return "register";
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email này đã được sử dụng!");
            return "register";
        }

        // Kiểm tra mật khẩu có để trống không
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            bindingResult.rejectValue("password", "error.user", "Mật khẩu không được để trống!");
            return "register";
        }

        // Kiểm tra họ tên có để trống không
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            bindingResult.rejectValue("fullName", "error.user", "Họ và tên không được để trống!");
            return "register";
        }

        // Thiết lập thông tin mặc định cho user mới
        user.setUsername(user.getEmail());  // Dùng email làm username
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // ĐÃ SỬA DÒNG NÀY
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        // Lưu vào database
        userRepository.save(user);

        // Thông báo thành công và chuyển hướng về trang đăng nhập
        return "redirect:/login?registerSuccess=true";
    }
}