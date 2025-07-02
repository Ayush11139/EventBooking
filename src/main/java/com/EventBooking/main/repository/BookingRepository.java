package com.EventBooking.main.repository;

import com.EventBooking.main.entity.Booking;
import com.EventBooking.main.entity.Customer;
import com.EventBooking.main.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserAndEvent(Customer user, Event event);
    Optional<Booking> findByIdAndStatus(Long id, Booking.BookingStatus status);
} 