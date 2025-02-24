package com.xpoint.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String contactNo;  // Updated field name to camelCase

    private String address;    // Updated field name to camelCase

    private String profilePic; // Updated field name to camelCase

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters (optional, since you're using Lombok)
}
