package com.xpoint.demo.WebSocketConfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "user_shop_exchange";
     private String statusQueue;
      private String routingKey;
     
     
    @Bean
    public TopicExchange userShopExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "customRabbitTemplate")
    public AmqpTemplate customRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    
    
 //STATUS RECIVING 
    
    @PostConstruct
    public void initializeQueueAndRoutingKey() {
        this.statusQueue = "status_queue_user" + 6 + "_shop" + 6;
        this.routingKey = "status_route_user" + 6 + "_shop" + 6;
    } 
    
    
@Bean
public Queue statusUserQueue() {
    return new Queue(statusQueue, true); // Durable queue
}

@Bean
public Binding statusUserBinding(Queue statusUserQueue, TopicExchange exchange) {
    return BindingBuilder.bind(statusUserQueue).to(exchange).with(routingKey);
}
    
    public String  getStatusQueueName() {
    	
    	return statusQueue;
    }
    
    
   
    
    
    
}
