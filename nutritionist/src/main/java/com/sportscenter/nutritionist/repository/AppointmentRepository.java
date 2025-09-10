package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAppointmentsByPatientId(int id);
    List<Appointment> findAppointmentsByNutritionistId(int id);
    @Query("SELECT a FROM Appointment a WHERE a.dateAndTime = :dateAndTime AND a.nutritionistId = :nutritionistId")
    List<Appointment> findAppointmentsByDateAndTimeAndNutritionistId(@Param("dateAndTime") Date dateAndTime,@Param("nutritionistId") int nutritionistId);
    List<Appointment> findAppointmentsByDateBooked(Date date);
    @Query("SELECT a FROM Appointment a WHERE a.dateAndTime BETWEEN :currentDate AND :twoDaysLater AND a.isAvailable = false")
    List<Appointment> findAppointmentsInNext2Days(@Param("currentDate") Date currentDate,@Param("twoDaysLater") Date twoDaysLater);

}
