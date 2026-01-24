// package com.example.genderhealthcare.controller;

// import com.example.genderhealthcare.dtos.UserUpdateRequest;
// import com.example.genderhealthcare.entity.Role;
// import com.example.genderhealthcare.entity.User;
// import com.example.genderhealthcare.repository.UserRepository;
package com.example.SP26SE025.controller;

import com.example.SP26SE025.dtos.UserUpdateRequest;
import com.example.SP26SE025.entity.Role;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    // ================= GET ALL USERS =================
    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // ================= ENABLE / DISABLE USER =================
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        // u.setActive(!u.isActive()); // ✅ TOGGLE THẬT
        userRepository.save(u);
        return ResponseEntity.ok().build();
    }

    // ================= UPDATE FULL INFO =================
 @PutMapping("/{id}")
public ResponseEntity<?> updateUser(
        @PathVariable Long id,
        @RequestBody UserUpdateRequest req) {

    User u = userRepository.findById(id).orElseThrow();

    u.setFullName(req.getFullName());
    u.setEmail(req.getEmail());

    if (req.getRole() == null) {
        return ResponseEntity.badRequest().body("Role is required");
    }

    try {
        Role role = Role.valueOf(req.getRole().toUpperCase().trim());
        u.setRole(role);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body("Invalid role value: " + req.getRole());
    }

    userRepository.save(u);
    return ResponseEntity.ok().build();
}


    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}