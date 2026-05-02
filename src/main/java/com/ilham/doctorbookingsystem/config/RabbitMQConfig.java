package com.ilham.doctorbookingsystem.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "appointment.queue";
    public static final String EXCHANGE = "appointment.exchange";
    public static final String ROUTING_KEY = "appointment.routing";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

//    @Bean
//    public Jackson2JsonMessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

    @Bean
    public org.springframework.amqp.support.converter.MessageConverter messageConverter() {
        return new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter();
    }
}