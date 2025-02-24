package com.xpoint.demo.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.xpoint.demo.enumstore.MessageType;


@Data
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    private String fileName;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String fileUrl;

    private Long fileSize;

    private LocalDateTime uploadedAt = LocalDateTime.now();

     private Long pageCount;
    // Getters and Setters
}
