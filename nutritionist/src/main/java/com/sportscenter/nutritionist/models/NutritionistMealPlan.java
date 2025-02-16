package com.sportscenter.nutritionist.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionistMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public int nutritionistId;
    public int patientId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String mealPlan;
}