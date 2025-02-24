package com.xpoint.demo.ServiceImp;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrintJobProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendPrintJob(String message) {
        rabbitTemplate.convertAndSend("jobExchange", "jobRoutingKey", message);
        System.out.println("Message sent: " + message);
    }
}