package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Integer> {
}
