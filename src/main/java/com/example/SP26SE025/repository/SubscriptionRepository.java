package com.example.SP26SE025.repository;

import com.example.SP26SE025.entity.Subscription;
import com.example.SP26SE025.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserAndStatus(User user, String status);
    List<Subscription> findByUserOrderByStartDateDesc(User user);

    // =================================================================
    // --- CÁC HÀM THỐNG KÊ 
    // =================================================================

    @Query("SELECT MONTH(s.startDate), SUM(s.price) FROM Subscription s WHERE YEAR(s.startDate) = :year GROUP BY MONTH(s.startDate)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    @Query("SELECT s.planName, COUNT(s) FROM Subscription s WHERE YEAR(s.startDate) = :year GROUP BY s.planName")
    List<Object[]> getPackageUsageStatsByYear(@Param("year") int year);
    
    @Query("SELECT SUM(s.price) FROM Subscription s WHERE YEAR(s.startDate) = :year")
    Long getTotalRevenueByYear(@Param("year") int year);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE YEAR(s.startDate) = :year")
    Long countByYear(@Param("year") int year);

    List<Subscription> findByUserIdOrderByIdDesc(Long userId);
}