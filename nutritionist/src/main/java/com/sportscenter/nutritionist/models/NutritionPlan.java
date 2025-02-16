package com.sportscenter.nutritionist.models;

import com.sportscenter.nutritionist.enums.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int menuNumber;
    private int day;
    private MealType mealType;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;
}
