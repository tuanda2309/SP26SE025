package com.example.SP26SE025.controller;
import com.example.SP26SE025.entity.ServicePackage;
import com.example.SP26SE025.repository.ServicePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/admin/api/packages")
public class AdminServicePackageController {

    @Autowired
    private ServicePackageRepository packageRepository;

    // ================= GET ALL =================
    @GetMapping
    public List<ServicePackage> getAll() {
        return packageRepository.findAll();
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ServicePackage pkg) {

        pkg.setId(null);          // đảm bảo là tạo mới
        pkg.setActive(true);      // mặc định active

        if (pkg.isPopular() == false) {
            pkg.setPopular(false);
        }

        packageRepository.save(pkg);
        return ResponseEntity.ok().build();
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ServicePackage req) {

        ServicePackage pkg = packageRepository.findById(id).orElseThrow();

        pkg.setPackageName(req.getPackageName());
        pkg.setPrice(req.getPrice());
        pkg.setPeriod(req.getPeriod());
        pkg.setDescription(req.getDescription());
        pkg.setFeatures(req.getFeatures());
        pkg.setPopular(req.isPopular());
        pkg.setActive(req.isActive());

        packageRepository.save(pkg);
        return ResponseEntity.ok().build();
    }

    // ================= SOFT DELETE (DISABLE) =================
    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disable(@PathVariable Long id) {
        ServicePackage pkg = packageRepository.findById(id).orElseThrow();
        pkg.setActive(false);
        packageRepository.save(pkg);
        return ResponseEntity.ok().build();
    }

    // ================= ENABLE =================
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enable(@PathVariable Long id) {
        ServicePackage pkg = packageRepository.findById(id).orElseThrow();
        pkg.setActive(true);
        packageRepository.save(pkg);
        return ResponseEntity.ok().build();
    }

    // ================= HARD DELETE (NẾU GV YÊU CẦU) =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        packageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
