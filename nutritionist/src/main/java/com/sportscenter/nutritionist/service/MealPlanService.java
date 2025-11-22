package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.mealPlan.CreatedMealPlanDto;
import com.sportscenter.nutritionist.dto.mealPlan.NutritionistMealPlanDto;
import com.sportscenter.nutritionist.models.NutritionistMealPlan;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface MealPlanService {
    public void generateMealPlan(int userId) throws MessagingException, IOException;
    public void createMealPlan(NutritionistMealPlanDto dto) throws IOException, MessagingException;
    public List<CreatedMealPlanDto> getCreatedNutritionistsMealPlans(int id);
}
