package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

import java.util.Date;

@Data
public class NutritionistComplaintsDto {
    private int id;
    private int appointmentId;
    private int nutritionistId;
    private String content;
    private String patientsFullName;
    private Date appointmentDate;
}
