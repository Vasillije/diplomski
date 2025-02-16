package com.sportscenter.nutritionist.dto.users;

import com.sportscenter.nutritionist.enums.Gender;
import com.sportscenter.nutritionist.enums.Goal;
import com.sportscenter.nutritionist.enums.PhysicalActivity;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Gender gender;
    private PhysicalActivity physicalActivity;
    private Double weight;
    private Double height;
    private Integer age;
    private Goal goal;
    private boolean disease;
}
