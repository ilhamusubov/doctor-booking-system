package com.ilham.doctorbookingsystem.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentMessage implements Serializable {

    private String email;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
    private String type;
}
