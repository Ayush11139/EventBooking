package com.EventBooking.main.service;

import com.EventBooking.main.entity.Seat;
import com.EventBooking.main.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByEvent(Long eventId) {
        System.out.println("[SeatService] Fetching seats for event id: " + eventId);
        return seatRepository.findByEventId(eventId);
    }

    public List<Seat> getSeatsByEventAndNumbers(Long eventId, List<Integer> seatNumbers) {
        System.out.println("[SeatService] Fetching seats for event id: " + eventId + " and seat numbers: " + seatNumbers);
        return seatRepository.findByEventIdAndSeatNumberIn(eventId, seatNumbers);
    }

    @Transactional
    public void updateSeats(List<Seat> seats) {
        System.out.println("[SeatService] Updating seats: " + seats.stream().map(Seat::getId).toList());
        seatRepository.saveAll(seats);
        System.out.println("[SeatService] Seats updated.");
    }
} 