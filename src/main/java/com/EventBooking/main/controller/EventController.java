package com.EventBooking.main.controller;

import com.EventBooking.main.entity.Event;
import com.EventBooking.main.entity.Seat;
import com.EventBooking.main.service.EventService;
import com.EventBooking.main.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = eventService.getEvent(id)
            .orElseThrow(() -> {
                System.out.println("[Controller] Event not found for id: " + id);
                return new IllegalArgumentException("Event not found for id: " + id);
            });
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> getEventAvailability(@PathVariable Long id) {
        List<Seat> seats = seatService.getSeatsByEvent(id);
        if(seats.isEmpty()) {
            throw new IllegalArgumentException("No seats found for the event" + id);
        }
        long available = seats.stream().filter(s -> s.getStatus() == Seat.SeatStatus.AVAILABLE).count();
        long held = seats.stream().filter(s -> s.getStatus() == Seat.SeatStatus.HELD).count();
        long booked = seats.stream().filter(s -> s.getStatus() == Seat.SeatStatus.BOOKED).count();
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", id);
        map.put("availableSeats", available);
        map.put("holdSeats", held);
        map.put("bookingSeats", booked);
        map.put("availableSeatNumbers", seats.stream().filter(s -> s.getStatus() == Seat.SeatStatus.AVAILABLE).map(Seat::getSeatNumber).collect(Collectors.toList()));
        return ResponseEntity.ok(map);
    }
} 