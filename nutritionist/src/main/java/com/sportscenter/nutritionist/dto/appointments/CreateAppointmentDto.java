package com.sportscenter.nutritionist.dto.appointments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentDto {
    private Date dateAndTime;
    private int duration;
    private int nutritionistId;
}
