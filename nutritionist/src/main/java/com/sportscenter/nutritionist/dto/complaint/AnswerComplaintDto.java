package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

@Data
public class AnswerComplaintDto {
    private int complaintId;
    private int nutritionistId;
    private String content;
}
