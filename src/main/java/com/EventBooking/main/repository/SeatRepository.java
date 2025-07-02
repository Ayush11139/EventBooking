package com.EventBooking.main.repository;

import com.EventBooking.main.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventId(Long eventId);
    List<Seat> findByEventIdAndSeatNumberIn(Long eventId, List<Integer> seatNumbers);
} 