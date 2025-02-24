package com.xpoint.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpoint.demo.models.Attachment;
import com.xpoint.demo.models.Message;


@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByConversation_ConversationId(Long conversationId);
    Optional<Message>  findByMessageId(Long messageId);
    Message findTopByUserIdOrderBySentAtDesc(Long userId);

}
