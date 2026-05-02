package com.ilham.doctorbookingsystem.rabbitmq.concumer;

import com.ilham.doctorbookingsystem.config.RabbitMQConfig;
import com.ilham.doctorbookingsystem.rabbitmq.dto.AppointmentMessage;
import com.ilham.doctorbookingsystem.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(AppointmentMessage message) {

        try {

            switch (message.getType()) {

                case "CREATED":
                    emailService.sendAppointmentCreatedEmail(
                            message.getEmail(),
                            message.getDoctorName(),
                            LocalDate.parse(message.getDate()),
                            LocalTime.parse(message.getTime())
                    );
                    break;

                case "CONFIRMED":
                    emailService.sendAppointmentConfirmedEmail(
                            message.getEmail(),
                            message.getDoctorName(),
                            LocalDate.parse(message.getDate()),
                            LocalTime.parse(message.getTime())
                    );
                    break;

                case "CANCELLED":
                    emailService.sendAppointmentCanceledEmail(
                            message.getEmail(),
                            message.getDoctorName(),
                            LocalDate.parse(message.getDate()),
                            LocalTime.parse(message.getTime())
                    );
                    break;
            }

            System.out.println("EMAIL SENT: " + message.getType());

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}

