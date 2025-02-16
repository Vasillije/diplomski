package com.sportscenter.nutritionist.dto.users;

import lombok.Data;

@Data
public class ContactInfoDto {
    private int id;
    private String email;
    private String phoneNumber;
    private String address;
}
