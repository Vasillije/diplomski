package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.appointments.*;

import java.util.List;

public interface AppointmentService {
    public CreateAppointmentDto createAppointment(CreateAppointmentDto dto);
    public List<AvailableAppointmentDto> getAvailableAppointments();
    public List<PatientAppointmentDto> getAppointmentsByPatientId(int id);
    public List<NutritionistAppointmentDto> getAppointmentsByNutritionistId(int id);
    //public void bookAppointment(BookAppointmentDto dto);
    public Boolean isAppointmentOverlapping(CreateAppointmentDto dto);
    public void cancelAppointmentPatient(CancelAppointmentDto dto);
    public void cancelAppointmentNutritionist(int appointmentId);
    public List<PastBookedAppointmentsByNutritionistIdDto> getPastBookedExaminationsByNutritionistId(int id);
    public List<AppointmentsByDateDto> getAppointmentsByDate(String date, int id);
    public void book(BookDto dto);
    public AppointmentInfoDto getAppointmentInfo(int id);
    public void notifyAppointmentIn2Days();
    public void createDailyAppointments(String date, int nutritionistId);
    public List<NutritionistAppointmentDto> getAvailableAppointmentsByNutritionistId(int id);
}
