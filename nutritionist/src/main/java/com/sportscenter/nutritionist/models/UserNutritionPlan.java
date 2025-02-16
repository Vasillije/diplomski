package com.sportscenter.nutritionist.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserNutritionPlanKey.class)
public class UserNutritionPlan {
    @Id
    private int userId;

    @Id
    private int nutritionPlanId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "nutritionPlanId", referencedColumnName = "id", insertable = false, updatable = false)
    private NutritionPlan nutritionPlan;

    private double grams;
    private double kcal;

    public UserNutritionPlan(UserEntity user, NutritionPlan nutritionPlan, double grams, double kcal) {
        this.user = user;
        this.nutritionPlan = nutritionPlan;
        this.grams = grams;
        this.kcal = kcal;
        this.userId = user.getId();
        this.nutritionPlanId = nutritionPlan.getId();
    }
}
