package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

import java.util.Date;

@Data
public class CreateComplaintDto {
    private int appointmentId;
    private String content;
    private int patientId;
    private Date date;
}
