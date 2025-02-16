package com.sportscenter.nutritionist.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String day;
    public String breakfast;
    public String morningSnack;
    public String lunch;
    public String eveningSnack;
    public String dinner;

    @ManyToOne
    @JoinColumn(name = "nutritionist_meal_plan_id", nullable = false)
    private NutritionistMealPlan nutritionistMealPlan;
}
