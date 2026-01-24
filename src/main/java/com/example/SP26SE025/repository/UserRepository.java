package com.example.SP26SE025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SP26SE025.entity.User;
import com.example.SP26SE025.entity.Role; 
import java.util.List;                     
import java.util.Optional;                 


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);

    // Tìm user theo Role (Để lấy danh sách Bác sĩ)
    List<User> findByRole(Role role);
}