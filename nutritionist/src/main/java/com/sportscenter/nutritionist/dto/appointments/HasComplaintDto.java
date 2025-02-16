package com.sportscenter.nutritionist.dto.appointments;

import lombok.Data;

@Data
public class HasComplaintDto {
    private int appointmentId;
    private boolean hasGotComplaint;
}
