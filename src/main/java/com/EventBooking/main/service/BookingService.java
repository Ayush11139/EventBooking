package com.EventBooking.main.service;

import com.EventBooking.main.entity.*;
import com.EventBooking.main.repository.BookingRepository;
import com.EventBooking.main.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Booking confirmBooking(Hold hold) {
        System.out.println("[BookingService] Confirming booking for hold id: " + hold.getId());
        List<Booking> existing = bookingRepository.findByUserAndEvent(hold.getUser(), hold.getEvent());
        for (Booking b : existing) {
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED) {
                System.out.println("Customer already has a confirmed booking for this event");
                throw new IllegalArgumentException("Customer already has a confirmed booking for this event");
            }
        }
        Set<Seat> seats = hold.getHoldSeats().stream().map(HoldSeat::getSeat).collect(java.util.stream.Collectors.toSet());
        for (Seat seat : seats) {
            if (seat.getStatus() != Seat.SeatStatus.HELD) {
                System.out.println("[BookingService] Seat " + seat.getSeatNumber() + " is not held");
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " is not held");
            }
            seat.setStatus(Seat.SeatStatus.BOOKED);
        }
        seatRepository.saveAll(seats);
        Booking booking = Booking.builder()
                .user(hold.getUser())
                .event(hold.getEvent())
                .status(Booking.BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();
        Set<BookingSeat> bookingSeats = new HashSet<>();
        for (Seat seat : seats) {
            BookingSeat bookingSeat = BookingSeat.builder()
                    .booking(booking)
                    .seat(seat)
                    .build();
            bookingSeats.add(bookingSeat);
        }
        booking.setBookingSeats(bookingSeats);
        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("[BookingService] Booking confirmed with id: " + savedBooking.getId());
        return savedBooking;
    }

    public Optional<Booking> getBooking(Long bookingId) {
        System.out.println("[BookingService] Fetching booking with id: " + bookingId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            System.out.println("[BookingService] Booking not found for id: " + bookingId);
        }
        return booking;
    }

    @Transactional
    public void cancelBooking(Booking booking) {
        System.out.println("[BookingService] Canceling booking with id: " + booking.getId());
        booking.setStatus(Booking.BookingStatus.CANCELED);
        for (BookingSeat bookingSeat : booking.getBookingSeats()) {
            Seat seat = bookingSeat.getSeat();
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
        }
        seatRepository.saveAll(booking.getBookingSeats().stream().map(BookingSeat::getSeat).toList());
        bookingRepository.save(booking);
        System.out.println("[BookingService] Booking canceled and seats released for booking id: " + booking.getId());
    }
} 