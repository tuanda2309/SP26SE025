package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    // Repository này để lấy danh sách hồ sơ bệnh nhân (Customer)
}