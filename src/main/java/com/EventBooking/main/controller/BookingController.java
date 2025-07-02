package com.EventBooking.main.controller;

import com.EventBooking.main.entity.Booking;
import com.EventBooking.main.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id)
                .map(booking -> ResponseEntity.ok(new BookingDTO(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        Booking booking = bookingService.getBooking(id).orElseThrow();
        bookingService.cancelBooking(booking);
        return ResponseEntity.ok().build();
    }

    // DTO for Booking
    public static class BookingDTO {
        private Long id;
        private String userEmail;
        private Long eventId;
        private String status;
        private java.util.List<Integer> seatNumbers;

        public BookingDTO(Booking booking) {
            this.id = booking.getId();
            this.userEmail = booking.getUser().getEmail();
            this.eventId = booking.getEvent().getId();
            this.status = booking.getStatus().name();
            this.seatNumbers = booking.getBookingSeats().stream()
                    .map(bs -> bs.getSeat().getSeatNumber())
                    .collect(Collectors.toList());
        }

        public Long getId() { return id; }
        public String getUserEmail() { return userEmail; }
        public Long getEventId() { return eventId; }
        public String getStatus() { return status; }
        public java.util.List<Integer> getSeatNumbers() { return seatNumbers; }
    }
} 