package com.kamruddin.modulith.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    // Define exchange name constants
    public static final String MODULITH_EXCHANGE = "modulith";
    public static final String ORDER_EVENTS_QUEUE = "order.events.queue";
    public static final String ORDER_EVENTS_ROUTING_KEY = "order.*";
    
    @Bean
    public TopicExchange exchange() {
        // Create a Topic exchange with the application name
        return new TopicExchange(MODULITH_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue orderEventsQueue() {
        // Create a durable queue for order events
        // This queue will be shared across all instances
        return new Queue(ORDER_EVENTS_QUEUE, true);
    }
    
    @Bean
    public Binding binding() {
        // Bind the queue to the exchange with a routing key pattern
        return BindingBuilder.bind(orderEventsQueue())
                .to(exchange())
                .with(ORDER_EVENTS_ROUTING_KEY);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(2); // Configure for concurrency
        factory.setMaxConcurrentConsumers(5);
        // Enable manual acknowledgment to ensure messages are processed
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        return factory;
    }
    
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }
}