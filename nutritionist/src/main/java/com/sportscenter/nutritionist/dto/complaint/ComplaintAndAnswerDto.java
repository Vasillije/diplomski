package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

@Data
public class ComplaintAndAnswerDto {
    private int complaintId;
    private String complaintContent;
    private int answerId;
    private String answerContent;
    private int nutritionistId;
    private int patientId;
    private int appointmentId;
}
