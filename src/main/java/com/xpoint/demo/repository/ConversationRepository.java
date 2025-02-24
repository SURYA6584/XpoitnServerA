package com.xpoint.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpoint.demo.models.Attachment;
import com.xpoint.demo.models.Conversation;
import com.xpoint.demo.models.Message;
import com.xpoint.demo.models.Shop;
import com.xpoint.demo.models.User;


@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    Optional<Conversation> findById(Long conversationId);

    Optional<Conversation> findByUser_UserIdAndShop_ShopId(Long userId, Long shopId);
 // Method to find all conversations for a specific shop
    @Query("SELECT c FROM Conversation c WHERE c.shop.shopId = :shopId")
    List<Conversation> findAllByShopId(Long shopId);
	
    
    
    @Query("SELECT DISTINCT c.user FROM Conversation c " +
            "JOIN c.messages m " +
            "WHERE c.shop.shopId = :shopId " +
            "GROUP BY c.user " +
            "ORDER BY MAX(m.sentAt) DESC")
     List<User> findUsersSortedByLatestMessage(@Param("shopId") Long shopId);
    
    
    @Query("""
            SELECT c.shop FROM Conversation c
            JOIN c.messages m
            WHERE c.user.userId = :userId
            GROUP BY c.shop
            ORDER BY MAX(m.sentAt) DESC
            """)
    List<Shop> findTop5ShopsByUserIdSortedByLatestMessage(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT m FROM Message m 
            JOIN m.conversation c 
            WHERE c.user.userId = :userId AND c.shop.shopId = :shopId 
            ORDER BY m.sentAt DESC LIMIT 1
            """)
    Optional<Message> findLatestMessageByUserIdAndShopId(@Param("userId") Long userId, @Param("shopId") Long shopId);

}
