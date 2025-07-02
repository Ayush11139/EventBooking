package com.EventBooking.main.service;

import com.EventBooking.main.entity.Customer;
import com.EventBooking.main.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository userRepository;

    public Customer getOrCreateUser(String email) {
        System.out.println("[CustomerService] Getting or creating user with email: " + email);
        try {
            return userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.save(Customer.builder().email(email).build()));
        } catch (Exception e) {
            System.out.println("[CustomerService] Error creating or fetching user: " + e.getMessage());
            throw new RuntimeException("Failed to get or create user: " + e.getMessage(), e);
        }
    }
} 