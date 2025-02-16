package com.sportscenter.nutritionist.dto.mealPlan;

import lombok.Data;

@Data
public class NutritionistMealPlanDto {
    private int nutritionistId;
    private int patientId;
    private String content;
    private int appointmentId;
}
