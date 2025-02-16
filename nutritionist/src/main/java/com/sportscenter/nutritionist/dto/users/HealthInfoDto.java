package com.sportscenter.nutritionist.dto.users;

import lombok.Data;

import java.util.List;

@Data
public class HealthInfoDto {
    private int id;
    private Integer gender;
    private Integer physicalActivity;
    private Double weight;
    private Double height;
    private Integer age;
    private int goal;
    private Boolean diseases;
}
