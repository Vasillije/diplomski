package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentInfoDto {
    private String nutritionistFullName;
    private Date appointmentDate;
    private String type;
}
