package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.ClinicProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<ClinicProfile, Long> {

    
}