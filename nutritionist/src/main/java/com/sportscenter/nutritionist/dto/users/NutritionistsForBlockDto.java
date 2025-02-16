package com.sportscenter.nutritionist.dto.users;

import lombok.Data;

@Data
public class NutritionistsForBlockDto {
    private int id;
    private String username;
    private String name;
    private String lastName;
    private String email;
    private int penaltyPoints;
}
