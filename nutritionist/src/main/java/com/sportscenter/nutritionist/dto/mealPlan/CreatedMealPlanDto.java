package com.sportscenter.nutritionist.dto.mealPlan;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.Date;

@Data
public class CreatedMealPlanDto {
    public int id;
    public String patientFullName;
    private String mealPlan;
    private Date dateCreated;
}
