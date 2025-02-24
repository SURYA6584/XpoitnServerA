package com.xpoint.demo.ServiceImp;


import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.xpoint.demo.models.Conversation;
import com.xpoint.demo.models.Message;
import com.xpoint.demo.models.Shop;
import com.xpoint.demo.models.User;
import com.xpoint.demo.repository.ConversationRepository;
import com.xpoint.demo.repository.MessageRepository;
import com.xpoint.demo.repository.ShopRepository;
import com.xpoint.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.xpoint.demo.Service.ChatService;
import com.xpoint.demo.enumstore.MessageStatus;
import com.xpoint.demo.enumstore.MessageType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImp implements ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    // Start a conversation between a user and a shop (or retrieve if exists)
    public Long startConversation(Long userId, Long shopId) {
        // Check if the conversation already exists
        Optional<Conversation> existingConversationOpt = conversationRepository.findByUser_UserIdAndShop_ShopId(userId, shopId);
        
        if (existingConversationOpt.isPresent()) {
            return existingConversationOpt.get().getConversationId(); // Return the existing conversation
        }

        // Fetch user and shop details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found with ID: " + shopId));

        // Create and save new conversation
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setShop(shop);
        
        // Save and return the new conversation's ID
        Conversation savedConversation = conversationRepository.save(conversation);
        return savedConversation.getConversationId();
    }

    @Transactional
    public Message sendMessage(Message message) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(message.getConversationId());
        if (!conversationOpt.isPresent()) {
            throw new RuntimeException("Conversation not found");
        }

        // Eagerly load messages if needed
        Conversation conversation = conversationOpt.get();
        Hibernate.initialize(conversation.getMessages());

        // Create and prepare the new message
        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setSenderId(message.getSenderId());
        msg.setSenderType(message.getSenderType());
        msg.setMessageType(message.getMessageType());
        msg.setStatus(MessageStatus.SENT);

        // Set the message content based on MessageType
        if (message.getMessageType() == MessageType.TEXT) {
            msg.setMessageText(message.getMessageText()); // Set text if it's a text message
            msg.setAttachment(null); // Ensure attachment is null
        } else  {
            msg.setAttachment(message.getAttachment()); // Set attachment if it's a file message
            msg.setMessageText(null); // Ensure text is null
        }

        // Save and return the message
        return messageRepository.save(msg);
    }

    
    
    
    
    

    public List<Message> getMessages(Long userId, Long shopId) {
        Optional<Conversation> conversationOpt = conversationRepository.findByUser_UserIdAndShop_ShopId(userId, shopId);

        if (!conversationOpt.isPresent()) {
            return null; // or throw an exception if you want
        }

        Long conversationId = conversationOpt.get().getConversationId();
        return messageRepository.findByConversation_ConversationId(conversationId);
    }

    public Message getLatestMessageByUser(Long userId) {
        return messageRepository.findTopByUserIdOrderBySentAtDesc(userId);
    }

    
    
//    public List<User> getUsersByShopId(Long shopId) {
//        List<Conversation> conversations = conversationRepository.findAllByShopId(shopId);
//        
//        // Extract users from conversations
//        return conversations.stream()
//                .map(Conversation::getUser)
//                .distinct() // Optional: to avoid duplicate users
//                .collect(Collectors.toList());
//    }
    
    
    public List<User> getUsersByShopIdSorted(Long shopId) {
        return conversationRepository.findUsersSortedByLatestMessage(shopId);
    }
    
    
    public List<Shop> getLatestShops(Long userId) {
        Pageable topFive = PageRequest.of(0, 5);
        return conversationRepository.findTop5ShopsByUserIdSortedByLatestMessage(userId, topFive);
    }


    
    
    public Optional<Message> getLatestMessage(Long userId, Long shopId) {
        return conversationRepository.findLatestMessageByUserIdAndShopId(userId, shopId);
    }

}
