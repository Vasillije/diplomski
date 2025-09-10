package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

import java.util.Date;

@Data
public class PastBookedAppointmentsByNutritionistIdDto {
    private int appointmentId;
    private int patientId;
    private int duration;
    private Date date;
    private String patientsFullName;
    private Date dateBooked;
}
