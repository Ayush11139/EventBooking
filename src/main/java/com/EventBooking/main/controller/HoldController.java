package com.EventBooking.main.controller;

import com.EventBooking.main.entity.*;
import com.EventBooking.main.service.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holds")
@RequiredArgsConstructor
public class HoldController {
    private final HoldService holdService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<?> holdSeats(@RequestBody HoldRequest request) {
        Customer user = customerService.getOrCreateUser(request.getUserEmail());
        Event event = eventService.getEvent(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found for id: " + request.getEventId()));
        Hold hold = holdService.holdSeats(user, event, request.getSeatNumbers());
        return ResponseEntity.ok(Map.of("holdId", hold.getId(), "expiresAt", hold.getExpiresAt()));
    }

    @PostMapping("/confirm/{holdId}")
    public ResponseEntity<?> confirmBooking(@PathVariable Long holdId) {
        Hold hold = holdService.getHold(holdId)
                .orElseThrow(() -> new IllegalArgumentException("Hold not found for id: " + holdId));
        Booking booking = bookingService.confirmBooking(hold);
        holdService.deleteHold(hold); // Only delete the hold, do not change seat status
        return ResponseEntity.ok(Map.of("bookingId", booking.getId()));
    }

    @Data
    public static class HoldRequest {
        private String userEmail;
        private Long eventId;
        private List<Integer> seatNumbers;
    }
} 