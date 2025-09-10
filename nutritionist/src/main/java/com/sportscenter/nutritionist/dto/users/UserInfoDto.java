package com.sportscenter.nutritionist.dto.users;

import lombok.Data;

@Data
public class UserInfoDto {
    private int patientId;
    private String username;
    private int gender;
    private int physicalActivity;
    private double weight;
    private double height;
    private int age;
    private int goal;
    private boolean diseases;
}
