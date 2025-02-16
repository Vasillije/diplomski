package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.UserEntity;
import com.sportscenter.nutritionist.models.UserNutritionPlan;
import com.sportscenter.nutritionist.models.UserNutritionPlanKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserNutritionPlanRepository extends JpaRepository<UserNutritionPlan, UserNutritionPlanKey> {
    List<UserNutritionPlan> findUserNutritionPlanByUser(UserEntity user);
}
