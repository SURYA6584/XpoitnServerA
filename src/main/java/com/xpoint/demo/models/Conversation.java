package com.xpoint.demo.models;




import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // The user involved in the conversation

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;  // The shop involved in the conversation

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Message> messages;  // Eager fetch
// List of messages in the conversation
}
