package com.sportscenter.nutritionist.models;

import java.io.Serializable;
import java.util.Objects;

public class UserNutritionPlanKey implements Serializable {
    private int userId;
    private int nutritionPlanId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNutritionPlanKey that = (UserNutritionPlanKey) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(nutritionPlanId, that.nutritionPlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nutritionPlanId);
    }


}
