package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

import java.util.Date;
@Data
public class PatientAppointmentDto {
    private int appointmentId;
    private Date date;
    private int duration;
    private String nutritionistFullName;
    private boolean hasComplaint;
    private Date dateBooked;
    private int nutritionistId;
    private String type;
}
