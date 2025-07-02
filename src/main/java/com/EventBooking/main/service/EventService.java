package com.EventBooking.main.service;

import com.EventBooking.main.entity.Event;
import com.EventBooking.main.entity.Seat;
import com.EventBooking.main.repository.EventRepository;
import com.EventBooking.main.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Event createEvent(Event event) {
        System.out.println("[EventService] Creating event: " + event.getName());
        try{
            Event savedEvent = eventRepository.save(event);
            Set<Seat> seats = new HashSet<>();
            for (int i = 1; i <= event.getTotalSeats(); i++) {
                Seat seat = Seat.builder()
                        .seatNumber(i)
                        .status(Seat.SeatStatus.AVAILABLE)
                        .event(savedEvent)
                        .build();
                seats.add(seat);
            }
            seatRepository.saveAll(seats);
            savedEvent.setSeats(seats);
            Event result = eventRepository.save(savedEvent);
            System.out.println("[EventService] Event created with id: " + result.getId());
            return result;
        } catch (Exception e) {
            System.out.println("[EventService] Error creating event: " + e.getMessage());
            throw new RuntimeException("Failed to create event: " + e.getMessage(), e);
        }
    }

    public List<Event> getAllEvents() {
        System.out.println("[EventService] Fetching all events");
        return eventRepository.findAll();
    }

    public Optional<Event> getEvent(Long id) {
        System.out.println("[EventService] Fetching event with id: " + id);
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            System.out.println("[EventService] Event not found for id: " + id);
        }
        return event;
    }

    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        System.out.println("[EventService] Updating event with id: " + id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found for id: " + id));
        event.setName(updatedEvent.getName());
        event.setDate(updatedEvent.getDate());
        event.setLocation(updatedEvent.getLocation());
        // Not updating totalSeats for simplicity
        Event saved = eventRepository.save(event);
        System.out.println("[EventService] Event updated with id: " + saved.getId());
        return saved;
    }

    @Transactional
    public void deleteEvent(Long id) {
        System.out.println("[EventService] Deleting event with id: " + id);
        eventRepository.deleteById(id);
        System.out.println("[EventService] Event deleted with id: " + id);
    }
} 