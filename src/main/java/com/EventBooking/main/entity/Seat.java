package com.EventBooking.main.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Event event;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookingSeat> bookingSeats;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HoldSeat> holdSeats;

    public enum SeatStatus {
        AVAILABLE, HELD, BOOKED
    }
} 