package com.example.SP26SE025.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.SP26SE025.entity.Role;
import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.repository.UserRepository;

// --- 1. Import thêm Entity và Repository của Gói dịch vụ ---
import com.example.SP26SE025.entity.ServicePackage;
import com.example.SP26SE025.repository.ServicePackageRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    // --- 2. Inject Repository gói dịch vụ ---
    @Autowired
    private ServicePackageRepository packageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createUserIfNotExists("admin@example.com", "123", Role.ADMIN);
        createUserIfNotExists("clinic@example.com", "123", Role.CLINIC); 
        createUserIfNotExists("doctor@example.com", "123", Role.DOCTOR);
        createUserIfNotExists("customer@example.com", "123", Role.CUSTOMER);

        // --- 3. Gọi hàm tạo Gói dịch vụ ---
        createPackagesIfNotExists();
    }

    // Hàm tạo User cũ (Giữ nguyên)
    private void createUserIfNotExists(String email, String rawPassword, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            String username = email.substring(0, email.indexOf("@")); 
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword)); 
            user.setRole(role);
            userRepository.save(user);
            System.out.println("Created user: " + email + " (" + username + ") with role " + role.name());
        }
    }

    // --- 4. Hàm mới: Tạo Gói dịch vụ mẫu ---
    private void createPackagesIfNotExists() {
        // Kiểm tra nếu bảng chưa có dữ liệu thì mới thêm vào
        if (packageRepository.count() == 0) {
            
            // Gói 1: Cơ Bản
            packageRepository.save(new ServicePackage(
                "Gói Cơ Bản",                 // Tên
                100000.0,                     // Giá
                "/lần",                       // Chu kỳ
                "Dành cho người mới bắt đầu", // Mô tả
                "1 Lần phân tích ảnh|Kết quả cơ bản", // Tính năng (ngăn cách bằng |)
                false,                        // Không phải gói HOT
                true                          // Đang hoạt động
            ));

            // Gói 2: Tiêu Chuẩn (HOT)
            packageRepository.save(new ServicePackage(
                "Gói Tiêu Chuẩn", 
                500000.0, 
                "/tháng", 
                "Phổ biến nhất hiện nay",
                "10 Lần phân tích ảnh|Báo cáo chi tiết (PDF)|Lưu trữ hồ sơ 1 năm|Hỗ trợ ưu tiên", 
                true, // Là gói HOT (isPopular = true)
                true
            ));

            // Gói 3: Cao Cấp
            packageRepository.save(new ServicePackage(
                "Gói Cao Cấp", 
                2000000.0, 
                "/năm", 
                "Giải pháp toàn diện",
                "Không giới hạn phân tích|Tất cả tính năng Tiêu chuẩn|Chat trực tiếp bác sĩ|Theo dõi tiến triển bệnh", 
                false, 
                true
            ));
            
            System.out.println(">>> Đã tạo dữ liệu mẫu cho Gói Dịch Vụ (Service Packages)!");
        }
    }
}