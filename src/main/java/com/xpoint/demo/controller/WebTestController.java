package com.xpoint.demo.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.xpoint.demo.models.WebCheck;
import java.util.Date;

@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"}) // Allow specific origin
@Controller
public class WebTestController {
	
	
	
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public WebCheck sendMessage(@Payload WebCheck webCheck) {
		
		webCheck.setTimestamp(new Date());
		return webCheck;
		
		
	}
	
	
	

}
