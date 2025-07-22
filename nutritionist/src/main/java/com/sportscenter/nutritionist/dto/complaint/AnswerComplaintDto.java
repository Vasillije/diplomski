package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

import java.util.Date;

@Data
public class AnswerComplaintDto {
    private int complaintId;
    private int nutritionistId;
    private String content;
    private Date date;
}
