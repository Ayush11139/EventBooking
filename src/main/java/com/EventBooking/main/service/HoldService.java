package com.EventBooking.main.service;

import com.EventBooking.main.entity.*;
import com.EventBooking.main.repository.HoldRepository;
import com.EventBooking.main.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HoldService {
    private final HoldRepository holdRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Hold holdSeats(Customer user, Event event, List<Integer> seatNumbers) {
        System.out.println("[HoldService] Attempting to hold seats: " + seatNumbers + " for user: " + user.getEmail() + " at event: " + event.getId());
        List<Seat> allSeats = seatRepository.findByEventId(event.getId());
        long heldOrBooked = allSeats.stream()
            .filter(s -> s.getStatus() == Seat.SeatStatus.HELD || s.getStatus() == Seat.SeatStatus.BOOKED)
            .count();
        if (heldOrBooked + seatNumbers.size() > event.getTotalSeats()) {
            System.out.println("[HoldService] Cannot hold seats: would exceed event capacity.");
            throw new IllegalArgumentException("Cannot hold seats: total held and booked seats would exceed event capacity.");
        }
        List<Seat> seats = seatRepository.findByEventIdAndSeatNumberIn(event.getId(), seatNumbers);
        if (seats.size() != seatNumbers.size()) {
            System.out.println("[HoldService] Some requested seats do not exist.");
            throw new IllegalArgumentException("Some requested seats do not exist");
        }
        for (Seat seat : seats) {
            if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
                System.out.println("[HoldService] Seat " + seat.getSeatNumber() + " is not available");
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " is not available");
            }
        }
        for (Seat seat : seats) {
            seat.setStatus(Seat.SeatStatus.HELD);
        }
        seatRepository.saveAll(seats);
        Hold hold = Hold.builder()
                .user(user)
                .event(event)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        Set<HoldSeat> holdSeats = new HashSet<>();
        for (Seat seat : seats) {
            HoldSeat holdSeat = HoldSeat.builder()
                    .hold(hold)
                    .seat(seat)
                    .build();
            holdSeats.add(holdSeat);
        }
        hold.setHoldSeats(holdSeats);
        Hold savedHold = holdRepository.save(hold);
        System.out.println("[HoldService] Hold created with id: " + savedHold.getId() + ", expires at: " + savedHold.getExpiresAt());
        return savedHold;
    }

    public Optional<Hold> getHold(Long holdId) {
        System.out.println("[HoldService] Fetching hold with id: " + holdId);
        Optional<Hold> hold = holdRepository.findById(holdId);
        if (hold.isEmpty()) {
            System.out.println("[HoldService] Hold not found for id: " + holdId);
        }
        return hold;
    }

    @Transactional
    public void releaseHold(Hold hold) {
        System.out.println("[HoldService] Releasing hold with id: " + hold.getId());
        for (HoldSeat holdSeat : hold.getHoldSeats()) {
            Seat seat = holdSeat.getSeat();
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
        }
        seatRepository.saveAll(hold.getHoldSeats().stream().map(HoldSeat::getSeat).toList());
        holdRepository.delete(hold);
        System.out.println("[HoldService] Hold released and deleted: " + hold.getId());
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredHolds() {
        List<Hold> expiredHolds = holdRepository.findByExpiresAtBefore(LocalDateTime.now());
        System.out.println("[HoldService] Releasing " + expiredHolds.size() + " expired holds at " + java.time.LocalDateTime.now());
        for (Hold hold : expiredHolds) {
            releaseHold(hold);
        }
        System.out.println("[HoldService] Expired holds cleanup complete.");
    }

    public void deleteHold(Hold hold) {
        System.out.println("[HoldService] Deleting hold with id: " + hold.getId() + " (no seat status change)");
        holdRepository.delete(hold);
    }
} 