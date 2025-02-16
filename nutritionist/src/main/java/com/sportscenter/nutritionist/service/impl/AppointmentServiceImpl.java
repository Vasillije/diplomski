package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.dto.appointments.*;
import com.sportscenter.nutritionist.dto.users.NutritionistsForBlockDto;
import com.sportscenter.nutritionist.email.EmailSenderService;
import com.sportscenter.nutritionist.exceptions.AppointmentOverlapException;
import com.sportscenter.nutritionist.models.Appointment;
import com.sportscenter.nutritionist.models.NutritionistMealPlan;
import com.sportscenter.nutritionist.models.UserEntity;
import com.sportscenter.nutritionist.repository.AppointmentRepository;
import com.sportscenter.nutritionist.repository.UserRepository;
import com.sportscenter.nutritionist.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepository appointmentRepository;
    private UserRepository userRepository;
    private EmailSenderService senderService;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, UserRepository userRepository, EmailSenderService senderService){
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.senderService = senderService;
    }

    @Override
    public CreateAppointmentDto createAppointment(CreateAppointmentDto dto){
        var appointmentStartTime = dto.getDateAndTime().toInstant();
        var currentTime = new Date().toInstant();

        if(appointmentStartTime.isBefore(currentTime)){
            throw new IllegalArgumentException("Appointment start time cannot be in the past.");
        }

        Appointment appointment = new Appointment();
        appointment.setAvailable(true);
        appointment.setDateAndTime(dto.getDateAndTime());
        appointment.setDuration(dto.getDuration());
        appointment.setNutritionistId(dto.getNutritionistId());
        appointment.setHasComplaint(false);
        appointmentRepository.save(appointment);

        UserEntity user = userRepository.findById(dto.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<Integer> appointmentIds = user.getAppointmentIds();
        if (appointmentIds == null) {
            appointmentIds = new ArrayList<>();
        }
        appointmentIds.add(appointment.getId());
        user.setAppointmentIds(appointmentIds);
        userRepository.save(user);
        return dto;
    }

    @Override
    public Boolean isAppointmentOverlapping(CreateAppointmentDto dto){

        Date dNewStartTime = dto.getDateAndTime();
        Date dNewFinishTime = new Date(dto.getDateAndTime().getTime());
        dNewFinishTime.setTime(dNewFinishTime.getTime() + (long) dto.getDuration() * 60 * 1000);

        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(dto.getNutritionistId());

        for(Appointment a : appointments){
            Date dStartTime = a.getDateAndTime();
            Date dFinishTime = new Date(a.getDateAndTime().getTime());

            dFinishTime.setTime(dFinishTime.getTime() + (long) a.getDuration() * 60 * 1000);

            var newStartTime = dNewStartTime.toInstant();
            var newFinishTime = dNewFinishTime.toInstant();
            var startTime = dStartTime.toInstant();
            var finishTime = dFinishTime.toInstant();

            if(newStartTime.isBefore(startTime) && newFinishTime.isAfter(finishTime)){
                return true;
            }
            else if(newStartTime.isAfter(startTime) && newStartTime.isBefore(finishTime) && newFinishTime.isAfter(finishTime)){
                return true;
            }
            else if(newStartTime.isBefore(startTime) && newFinishTime.isAfter(startTime) && newFinishTime.isBefore(finishTime)){
                return true;
            }
            else if(newStartTime.isAfter(startTime) && newFinishTime.isBefore(finishTime)){
                return true;
            }
            else if(newStartTime.equals(startTime) || newStartTime.equals(finishTime) || newFinishTime.equals(startTime) || newFinishTime.equals(finishTime)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AvailableAppointmentDto> getAvailableAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        List<Appointment> futureAppointments = new ArrayList<>();
        List<Appointment> availableAppointments = new ArrayList<>();
        List<AvailableAppointmentDto> appointmentDtos = new ArrayList<>();
        Date currentDate = new Date();

        for(Appointment a : allAppointments){
            if(a.getDateAndTime().toInstant().isAfter(currentDate.toInstant())){
                futureAppointments.add(a);
            }
        }

        for(Appointment a : futureAppointments){
            if(a.isAvailable()){
                availableAppointments.add(a);
            }
        }

        for(Appointment a : availableAppointments){
            AvailableAppointmentDto dto = new AvailableAppointmentDto();
            int userId = a.getNutritionistId();
            UserEntity user = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            dto.setAppointmentId(a.getId());
            dto.setDate(a.getDateAndTime());
            dto.setDuration(a.getDuration());
            dto.setNutritionistId(a.getNutritionistId());
            dto.setNutritionistName(user.getName());
            dto.setNutritionistLastName(user.getLastname());

            appointmentDtos.add(dto);
        }
        return appointmentDtos;
    }
    @Override
    public List<PatientAppointmentDto> getAppointmentsByPatientId(int id){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByPatientId(id);
        List<PatientAppointmentDto> appointmentDtos = new ArrayList<>();
        for(Appointment a : appointments){
            UserEntity user = userRepository.findById(a.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));
            PatientAppointmentDto dto = new PatientAppointmentDto();
            dto.setAppointmentId(a.getId());
            dto.setDate(a.getDateAndTime());
            dto.setDuration(a.getDuration());
            dto.setNutritionistName(user.getName());
            dto.setNutritionistLastName(user.getLastname());
            dto.setHasComplaint(a.isHasComplaint());

            appointmentDtos.add(dto);
        }
        return appointmentDtos;
    }

    @Override
    public List<NutritionistAppointmentDto> getAppointmentsByNutritionistId(int id){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(id);
        List<NutritionistAppointmentDto> appointmentDtos = new ArrayList<>();

        for(Appointment a : appointments){
            NutritionistAppointmentDto dto = new NutritionistAppointmentDto();
            dto.setAppointmentId(a.getId());
            dto.setDate(a.getDateAndTime());
            dto.setDuration(a.getDuration());

            if(a.getPatientId() != 0) {
                UserEntity user = userRepository.findById(a.getPatientId()).orElseThrow(() -> new RuntimeException("User not found"));
                dto.setPatientName(user.getName());
                dto.setPatientLastName(user.getLastname());
            } else{
                dto.setPatientName("");
                dto.setPatientLastName("");
            }
            appointmentDtos.add(dto);
        }
        return appointmentDtos;
    }

    @Override
    public void bookAppointment(BookAppointmentDto dto){
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setPatientId(dto.getPatientId());
        appointment.setAvailable(false);
        appointmentRepository.save(appointment);

        UserEntity user = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<Integer> appointmentIds = user.getAppointmentIds();
        if(appointmentIds == null){
            appointmentIds = new ArrayList<>();
        }
        appointmentIds.add(appointment.getId());
        user.setAppointmentIds(appointmentIds);
        userRepository.save(user);

        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));

        senderService.sendEmail(nutritionist.getEmail(), "", "User with username "+ user.getUsername()+ " booked appointment with id "
                + appointment.getId());
    }

    @Override
    public void cancelAppointmentPatient(int appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity user = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("Nutritionist not found"));

        Date now = new Date();
        Date next24Hours = new Date(now.getTime() + (24 * 60 * 60 * 1000));

        appointment.setAvailable(true);
        appointment.setPatientId(0);

        if(appointment.getDateAndTime().before(next24Hours)){
            user.setPenaltyPoints(user.getPenaltyPoints() + 2);
        }
        else{
            user.setPenaltyPoints(user.getPenaltyPoints() + 1);
        }

        if(user.getPenaltyPoints() >= 5){
            user.setBlocked(true);
        }

        List<Integer> appointmentIds = user.getAppointmentIds();
        appointmentIds.remove(Integer.valueOf(appointment.getId()));
        user.setAppointmentIds(appointmentIds);

        appointmentRepository.save(appointment);
        userRepository.save(user);

        senderService.sendEmail(nutritionist.getEmail(), "", "User with username "+ user.getUsername()+
                " cancelled appointment with id "+ appointmentId);
    }

    @Override
    public void cancelAppointmentNutritionist(int appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        if(appointment.getPatientId() != 0) {
            UserEntity patient = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
            appointment.setPatientId(0);
            nutritionist.setPenaltyPoints(nutritionist.getPenaltyPoints() + 1);
            senderService.sendEmail("", "Appointment canceled", "Nutritionist with username "+ nutritionist.getUsername()+
                    " has cancelled appointment that starts at "+appointment.getDateAndTime());
            if(nutritionist.getPenaltyPoints() == 5){
                senderService.sendEmail("", "Block nutritionist", "Nutritionist with username "+ nutritionist.getUsername()+ " has " +
                        nutritionist.getPenaltyPoints() + " penalty points");
            }
        }
        appointmentRepository.delete(appointment);
    }

    @Override
    public List<PastBookedAppointmentsByNutritionistIdDto> getPastBookedAppointmentsByNutritionistId(int id){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(id);
        List<PastBookedAppointmentsByNutritionistIdDto> dtos = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();
        Date currentDate = new Date();

        for(Appointment a : appointments){
            if(!a.isAvailable() && a.getDateAndTime().toInstant().isBefore(currentDate.toInstant()) && a.getPatientId() != 0
            && a.getDuration() == 60 && !a.isHasMealPlan()){
                validAppointments.add(a);
            }
        }

        for(Appointment a : validAppointments){
            PastBookedAppointmentsByNutritionistIdDto dto = new PastBookedAppointmentsByNutritionistIdDto();
            dto.setAppointmentId(a.getId());
            dto.setDate(a.getDateAndTime());
            dto.setDuration(a.getDuration());
            dto.setPatientId(a.getPatientId());

            dtos.add(dto);
        }
        return dtos;
    }
}
