package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.mealPlan.NutritionistMealPlanDto;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface MealPlanService {
    public void generateMealPlan(int userId) throws MessagingException, IOException;
    public void createMealPlan(NutritionistMealPlanDto dto) throws IOException, MessagingException;
}
