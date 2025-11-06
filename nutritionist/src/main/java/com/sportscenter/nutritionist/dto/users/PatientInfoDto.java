package com.sportscenter.nutritionist.dto.users;

import com.sportscenter.nutritionist.enums.Gender;
import com.sportscenter.nutritionist.enums.Goal;
import com.sportscenter.nutritionist.enums.PhysicalActivity;
import lombok.Data;

@Data
public class PatientInfoDto {
    private int patientId;
    private Gender gender;
    private PhysicalActivity physicalActivity;
    private double weight;
    private double height;
    private int age;
    private Goal goal;
}
