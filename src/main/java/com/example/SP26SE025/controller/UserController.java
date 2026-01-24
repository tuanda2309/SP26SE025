package com.example.SP26SE025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.SP26SE025.entity.Role;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/userManager/newUserForm";
    }

    @PostMapping
    public String updateUser(@ModelAttribute User updatedUser) {
        User existingUser = userService.findById(updatedUser.getId());

        if (existingUser != null) {
            existingUser.setFullName(updatedUser.getFullName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setRole(updatedUser.getRole());
            userService.save(existingUser);
        }

        return "redirect:/admin/users";
    }

    // ✅ Tạo mới người dùng (chỉ dùng khi user chưa có ID)
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, Model model) {

        // Kiểm tra trùng email
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", "Email đã được sử dụng!");
            return "admin/userManager/newUserForm";
        }

        userService.save(user);
        return "redirect:/admin/users";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "admin/userManager/userForm";
        } else {
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }
}
