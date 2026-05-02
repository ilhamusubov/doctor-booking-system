package com.ilham.doctorbookingsystem.rabbitmq.producer;

import com.ilham.doctorbookingsystem.config.RabbitMQConfig;
import com.ilham.doctorbookingsystem.rabbitmq.dto.AppointmentMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendAppointment(AppointmentMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}
