package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.NutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Integer> {
    List<NutritionPlan> findNutritionPlansByMenuNumber(int menuNumber);
}
