package com.xpoint.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shopId;

    private String shopName;
    private String ownerName;

    @Column(unique = true)
    private String email;

    private String contactInfo;
    private String location;
    private String latitude;
    private String longitude;
    
    private String profile_pic;

    private String qrCodeUrl;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}
