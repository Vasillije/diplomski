package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

import java.util.Date;

@Data
public class NutritionistAppointmentDto {
    private int appointmentId;
    private Date date;
    private int duration;
    private String patientFullName;
}
