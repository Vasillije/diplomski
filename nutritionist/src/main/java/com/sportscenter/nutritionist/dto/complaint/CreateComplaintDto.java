package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

@Data
public class CreateComplaintDto {
    private int appointmentId;
    private String content;
    private int patientId;
}
