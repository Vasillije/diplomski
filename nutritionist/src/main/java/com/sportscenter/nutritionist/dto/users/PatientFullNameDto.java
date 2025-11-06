package com.sportscenter.nutritionist.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientFullNameDto {
    private int patientId;
    private String patientFullName;
}
