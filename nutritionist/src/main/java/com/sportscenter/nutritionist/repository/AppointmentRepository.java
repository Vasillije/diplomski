package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAppointmentsByPatientId(int id);
    List<Appointment> findAppointmentsByNutritionistId(int id);
}
