package com.sportscenter.nutritionist.models;

import com.sportscenter.nutritionist.enums.MealType;
import com.sportscenter.nutritionist.enums.Purpose;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double kcal;
    private double protein;
    private double fat;
    private double carbohydrates;
}
