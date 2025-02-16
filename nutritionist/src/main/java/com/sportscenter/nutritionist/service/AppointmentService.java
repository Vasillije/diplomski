package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.appointments.*;

import java.util.List;

public interface AppointmentService {
    public CreateAppointmentDto createAppointment(CreateAppointmentDto dto);
    public List<AvailableAppointmentDto> getAvailableAppointments();
    public List<PatientAppointmentDto> getAppointmentsByPatientId(int id);
    public List<NutritionistAppointmentDto> getAppointmentsByNutritionistId(int id);
    public void bookAppointment(BookAppointmentDto dto);
    public Boolean isAppointmentOverlapping(CreateAppointmentDto dto);
    public void cancelAppointmentPatient(int appointmentId);
    public void cancelAppointmentNutritionist(int appointmentId);
    public List<PastBookedAppointmentsByNutritionistIdDto> getPastBookedAppointmentsByNutritionistId(int id);
}
