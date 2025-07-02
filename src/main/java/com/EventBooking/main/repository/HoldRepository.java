package com.EventBooking.main.repository;

import com.EventBooking.main.entity.Hold;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HoldRepository extends JpaRepository<Hold, Long> {
    List<Hold> findByExpiresAtBefore(LocalDateTime now);
} 