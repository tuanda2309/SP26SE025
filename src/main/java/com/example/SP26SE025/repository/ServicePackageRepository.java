package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    // Lấy tất cả gói (Dùng cho Admin quản lý)
    // Lấy gói đang hoạt động (Dùng cho Khách hàng xem)
    List<ServicePackage> findByIsActiveTrue();
    List<ServicePackage> findAll();
}