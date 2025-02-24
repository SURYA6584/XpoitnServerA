package com.xpoint.demo.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpoint.demo.enumstore.FileType;
import com.xpoint.demo.enumstore.MessageStatus;
import com.xpoint.demo.enumstore.MessageType;
import com.xpoint.demo.enumstore.SenderType;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    // Column to store conversation ID directly
    @Column(name = "conversation_id", insertable = false, updatable = false)
    private Long conversationId;

    // Many-to-One relationship with Conversation entity, mapped to the same column
    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "conversationId")
    @JsonIgnore // Prevents serialization of the full conversation
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    @Column(name = "sender_id")
    private Long senderId;  // Can be user_id or shop_id

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String messageText;  // Null for non-text messages

    @ManyToOne
    @JoinColumn(name = "attachment_id", nullable = true)
    private Attachment attachment;  // Null for text messages

    private LocalDateTime sentAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.SENT;

    private Boolean isRead = false;

    // Getters and Setters
}
