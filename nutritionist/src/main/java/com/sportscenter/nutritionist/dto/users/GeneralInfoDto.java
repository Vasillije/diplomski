package com.sportscenter.nutritionist.dto.users;

import lombok.Data;

@Data
public class GeneralInfoDto {
    private int id;
    private String username;
    private String password;
    private String name;
    private String lastName;
}
