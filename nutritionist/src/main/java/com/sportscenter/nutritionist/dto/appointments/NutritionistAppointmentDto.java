package com.sportscenter.nutritionist.dto.appointments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionistAppointmentDto {
    private int appointmentId;
    private Date date;
    private int duration;
    private String patientFullName;
}
