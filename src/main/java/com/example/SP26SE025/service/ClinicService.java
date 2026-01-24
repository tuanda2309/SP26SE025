package com.example.SP26SE025.service;

import com.example.SP26SE025.entity.ServicePackage;
import com.example.SP26SE025.repository.ServicePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    @Autowired
    private ServicePackageRepository packageRepository;

    // Lấy danh sách tất cả gói
    public List<ServicePackage> getAllPackages() {
        return packageRepository.findAll();
    }

    // Tạo gói mới
    public void createPackage(ServicePackage pkg) {
        pkg.setActive(true); // Mặc định là Active
        packageRepository.save(pkg);
    }

    // Cập nhật gói
    public void updatePackage(Long id, ServicePackage updatedPkg) {
        ServicePackage existingPkg = packageRepository.findById(id).orElse(null);
        if (existingPkg != null) {
            existingPkg.setPackageName(updatedPkg.getPackageName());
            existingPkg.setPrice(updatedPkg.getPrice());
            existingPkg.setPeriod(updatedPkg.getPeriod());
            existingPkg.setDescription(updatedPkg.getDescription());
            existingPkg.setFeatures(updatedPkg.getFeatures());
            existingPkg.setPopular(updatedPkg.isPopular());
            packageRepository.save(existingPkg);
        }
    }

    // Ngừng cung cấp (Xóa mềm)
    public void togglePackageStatus(Long id) {
        ServicePackage pkg = packageRepository.findById(id).orElse(null);
        if (pkg != null) {
            pkg.setActive(!pkg.isActive()); // Đảo trạng thái (Active <-> Inactive)
            packageRepository.save(pkg);
        }
    }
}