package com.sportscenter.nutritionist.dto.complaint;

import com.fasterxml.jackson.databind.DatabindException;
import lombok.Data;

import java.util.Date;

@Data
public class ComplaintAndAnswerDto {
    private int complaintId;
    private String complaintContent;
    private int answerId;
    private String answerContent;
    private int nutritionistId;
    private int patientId;
    private int appointmentId;
    private String nutritionistFullName;
    private String patientFullName;
    private Date  complaintDate;
    private Date answerDate;
    private Date appointmentDate;
}
