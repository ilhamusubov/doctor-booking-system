package com.ilham.doctorbookingsystem.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("OTP Verification Code");
        message.setText("Your OTP code is: " + otp + "\nThis code is valid for 5 minutes.");

        mailSender.send(message);
    }

    public void sendAppointmentCreatedEmail(String to, String doctorName, LocalDate date, LocalTime time) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Appointment Created");

        message.setText(
                "Your appointment request has been created successfully.\n\n" +
                        "Doctor: " + doctorName + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Status: PENDING"
        );
        mailSender.send(message);
    }
}
