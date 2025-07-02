package com.EventBooking.main.repository;

import com.EventBooking.main.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
} 