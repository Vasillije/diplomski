package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

@Data
public class BookDto {
    private int appointmentId;
    private int patientId;
    private String type;
}
