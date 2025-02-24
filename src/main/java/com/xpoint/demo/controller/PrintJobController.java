package com.xpoint.demo.controller;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.xpoint.demo.WebSocketConfig.RabbitMQConfig;
import com.xpoint.demo.models.PrintRequest;
import com.xpoint.demo.models.User;
import org.springframework.amqp.core.Message;


@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"}) // Allow specific origin

@RestController
public class PrintJobController {

   
	
	 @Autowired
	  private SimpMessagingTemplate messagingTemplate;
	
	

	@Autowired
	RabbitMQConfig rabbitMQConfig;
	
	@Autowired
	private RabbitTemplate template;
	
	Long globleshopId;
    
	@PostMapping("/publish")
	public String publishMessage(@RequestBody List<PrintRequest> printRequestData   ) {

	    System.out.println("printRequestData::"+printRequestData);
	    
        String routingKey = "print_route_shop_"+6;

	    // Send the list of PrintRequests to RabbitMQ
	    template.convertAndSend(RabbitMQConfig.EXCHANGE,routingKey, printRequestData);

	    return "Messages Published";
	}
	
	@RabbitListener(queues = "#{@statusUserQueue.name}") // SpEL to dynamically resolve the queue name
	public void listener(String status) throws IOException {
	    messagingTemplate.convertAndSend("/topic/status", status);
	    System.out.println(status);
	}

	



}