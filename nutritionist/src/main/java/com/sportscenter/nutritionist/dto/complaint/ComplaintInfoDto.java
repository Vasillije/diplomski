package com.sportscenter.nutritionist.dto.complaint;

import lombok.Data;

import java.util.Date;

@Data
public class ComplaintInfoDto {
    public int id;
    public Date appointmentDate;
    public Date complaintDate;
    public String content;
    public String patientFullName;
}
