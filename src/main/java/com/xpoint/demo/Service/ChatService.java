package com.xpoint.demo.Service;

import java.util.List;
import java.util.Optional;

import com.xpoint.demo.enumstore.MessageStatus;
import com.xpoint.demo.enumstore.MessageType;
import com.xpoint.demo.enumstore.SenderType;
import com.xpoint.demo.models.Conversation;
import com.xpoint.demo.models.Message;
import com.xpoint.demo.models.Shop;
import com.xpoint.demo.models.User;

public interface ChatService {

	
	
    public Long startConversation(Long userId, Long shopId);

    public Message sendMessage(Message message);

    public List<Message> getMessages(Long userId, Long shopId);

    public List<User> getUsersByShopIdSorted(Long shopId);
    
    public Message getLatestMessageByUser(Long userId);

    
    public List<Shop> getLatestShops(Long userId);

    
    //used in Reactnative
    public Optional<Message> getLatestMessage(Long userId, Long shopId);


}
