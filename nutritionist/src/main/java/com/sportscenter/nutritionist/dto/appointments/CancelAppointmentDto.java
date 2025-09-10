package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

@Data
public class CancelAppointmentDto {
    private int appointmentId;
    private String type;
}
