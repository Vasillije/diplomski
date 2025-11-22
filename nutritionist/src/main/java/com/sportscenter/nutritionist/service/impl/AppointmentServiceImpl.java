package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.dto.appointments.*;
import com.sportscenter.nutritionist.email.EmailSenderService;
import com.sportscenter.nutritionist.models.Appointment;
import com.sportscenter.nutritionist.models.UserEntity;
import com.sportscenter.nutritionist.repository.AppointmentRepository;
import com.sportscenter.nutritionist.repository.UserRepository;
import com.sportscenter.nutritionist.service.AppointmentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private AppointmentRepository appointmentRepository;
    private UserRepository userRepository;
    private EmailSenderService senderService;
    private static final int CONTROL_OFFSET_MINUTES = 20;
    private static final int EXAMINATION_OFFSET_MINUTES = 40;

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

   /* @Override
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
    }*/

    @Override
    public Boolean isAppointmentOverlapping(CreateAppointmentDto dto) {
        Date dNewStartTime = dto.getDateAndTime();
        Date dNewFinishTime = new Date(dto.getDateAndTime().getTime());
        dNewFinishTime.setTime(dNewFinishTime.getTime() + (long) dto.getDuration() * 60 * 1000);

        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(dto.getNutritionistId());

        for (Appointment a : appointments) {
            Date dStartTime = a.getDateAndTime();
            Date dFinishTime = new Date(a.getDateAndTime().getTime());
            dFinishTime.setTime(dFinishTime.getTime() + (long) a.getDuration() * 60 * 1000);

            var newStartTime = dNewStartTime.toInstant();
            var newFinishTime = dNewFinishTime.toInstant();
            var startTime = dStartTime.toInstant();
            var finishTime = dFinishTime.toInstant();

            if (newStartTime.isBefore(finishTime) && newFinishTime.isAfter(startTime)) {
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
            dto.setNutritionistFullName(user.getName() + ' ' + user.getLastname());

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
            dto.setNutritionistFullName(user.getName() + ' ' + user.getLastname());
            dto.setHasComplaint(a.isHasComplaint());
            dto.setDateBooked(a.getDateBooked());
            dto.setNutritionistId(user.getId());
            dto.setType(a.getType());

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
                dto.setPatientFullName(user.getName() + " " + user.getLastname());
            } else{
                dto.setPatientFullName("");
            }
            appointmentDtos.add(dto);
        }
        return appointmentDtos;
    }

    @Override
    public void cancelAppointmentPatient(CancelAppointmentDto dto){
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity user = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("Nutritionist not found"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
        String formattedDate = sdf.format(appointment.getDateAndTime());

        Date now = new Date();
        Date next24Hours = new Date(now.getTime() + (24 * 60 * 60 * 1000));

        appointment.setAvailable(true);
        appointment.setPatientId(0);

        List<Appointment> nextAppointments = appointmentRepository.findAppointmentsByDateBooked(appointment.getDateBooked());

        List<Integer> appointmentIds = user.getAppointmentIds();
        appointmentIds.remove(Integer.valueOf(appointment.getId()));
        appointmentRepository.save(appointment);

        if(!nextAppointments.isEmpty()){
            for(Appointment a : nextAppointments){
                a.setAvailable(true);
                a.setPatientId(0);
                appointmentIds.remove(Integer.valueOf(a.getId()));
                appointmentRepository.save(a);
            }
        }

        user.setAppointmentIds(appointmentIds);

        if(appointment.getDateAndTime().before(next24Hours)){
            user.setPenaltyPoints(user.getPenaltyPoints() + 2);
        }
        else{
            user.setPenaltyPoints(user.getPenaltyPoints() + 1);
        }

        if(user.getPenaltyPoints() >= 5){
            user.setBlocked(true);
        }

        userRepository.save(user);

        String message = String.format(
                "Patient %s %s canceled %s starting at %s",
                user.getName(),
                user.getLastname(),
                dto.getType().toLowerCase(),
                formattedDate
        );

        senderService.sendEmail(nutritionist.getEmail(), "", message);
    }

    @Override
    public void cancelAppointmentNutritionist(int appointmentId){
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        if(appointment.getPatientId() != 0) {
            UserEntity patient = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
            List<Appointment> appointments = appointmentRepository.findAppointmentsByDateBooked(appointment.getDateBooked());
            if(!appointments.isEmpty()){
                for(Appointment a : appointments){
                    a.setPatientId(0);
                    a.setDateBooked(null);
                    a.setType(null);
                    a.setAvailable(true);
                    appointmentRepository.save(a);
                }
            }

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
    public List<PastBookedAppointmentsByNutritionistIdDto> getPastBookedExaminationsByNutritionistId(int id){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(id);
        List<PastBookedAppointmentsByNutritionistIdDto> dtos = new ArrayList<>();
        List<Appointment> validAppointments = new ArrayList<>();
        Date currentDate = new Date();

        for(Appointment a : appointments){
            if(!a.isAvailable() && a.getDateAndTime().toInstant().isBefore(currentDate.toInstant()) && a.getPatientId() != 0
            && "EXAMINATION".equals(a.getType()) && !a.isHasMealPlan()){
                validAppointments.add(a);
            }
        }

        for(Appointment a : validAppointments){
            UserEntity patient = userRepository.findById(a.getPatientId()).orElse(null);
            PastBookedAppointmentsByNutritionistIdDto dto = new PastBookedAppointmentsByNutritionistIdDto();
            dto.setAppointmentId(a.getId());
            dto.setDate(a.getDateAndTime());
            dto.setDuration(a.getDuration());
            dto.setPatientId(a.getPatientId());
            dto.setPatientsFullName(patient.getName() + ' ' + patient.getLastname());
            dto.setDateBooked(a.getDateBooked());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<AppointmentsByDateDto> getAppointmentsByDate(String date){
        List<Appointment> appointments = appointmentRepository.findAll();
        List<AppointmentsByDateDto> dtos = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date realDate;
        try {
            realDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Nevalidan format datuma, očekuje se yyyy-MM-dd");
        }

        for (Appointment a : appointments) {
            String appointmentDateStr = sdf.format(a.getDateAndTime());
            String filterDateStr = sdf.format(realDate);

            if (appointmentDateStr.equals(filterDateStr)) {
                AppointmentsByDateDto dto = new AppointmentsByDateDto();
                if(a.getPatientId() != 0) {
                    UserEntity patient = userRepository.findById(a.getPatientId()).orElse(null);
                    dto.setPatientName(patient.getName());
                    dto.setPatientLastName(patient.getLastname());

                }
                dto.setAppointmentId(a.getId());
                dto.setDate(a.getDateAndTime());
                dto.setDuration(a.getDuration());
                dtos.add(dto);
            }
        }

        return dtos;
    }

    @Override
    @Transactional
    public void book(BookDto dto){

        try {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() ->
                    new IllegalArgumentException("Termin sa ID " + dto.getAppointmentId() + " nije pronađen"));

            Date currentDate = new Date();
            int patientId = dto.getPatientId();
            int nutritionistId = appointment.getNutritionistId();
            Date appointmentDate = appointment.getDateAndTime();
            bookAppointment(patientId, appointment, currentDate, dto.getType());
            if(dto.getType().equals("CONTROL") || dto.getType().equals("EXAMINATION")){
                bookNextAppointment(appointmentDate, nutritionistId, patientId, currentDate, CONTROL_OFFSET_MINUTES, dto.getType());
                if(dto.getType().equals("EXAMINATION")) {
                    bookNextAppointment(appointmentDate, nutritionistId, patientId, currentDate, EXAMINATION_OFFSET_MINUTES, dto.getType());
                }
            }

            notifyNutritionist(patientId, nutritionistId, appointmentDate, dto.getType());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new IllegalStateException("Termin je već zauzet. Molimo osvežite stranicu i pokušajte ponovo.");
        }

    }

    public void bookNextAppointment(Date appointmentDate, int nutritionistId, int patientId, Date currentDate, int offsetMinutes, String type){
        Calendar cal = Calendar.getInstance();
        cal.setTime(appointmentDate);
        cal.add(Calendar.MINUTE, offsetMinutes);
        Date newDateTime = cal.getTime();
        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateAndTimeAndNutritionistId(newDateTime, nutritionistId);

        if (appointments.isEmpty()) {
            throw new IllegalArgumentException("Nije pronađen termin za nutritionistu " + nutritionistId +
                " u vreme " + newDateTime + " (offset: " + offsetMinutes + " minuta)");
        }

        bookAppointment(patientId, appointments.get(0), currentDate, type);
    }

    public void bookAppointment(int patientId, Appointment appointment, Date currentDate, String type){
        if (!appointment.isAvailable()) {
            throw new IllegalStateException("Termin je već zauzet");
        }

        appointment.setPatientId(patientId);
        appointment.setAvailable(false);
        appointment.setDateBooked(currentDate);
        appointment.setType(type);
        appointmentRepository.save(appointment);

        UserEntity user = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<Integer> appointmentIds = user.getAppointmentIds();
        if(appointmentIds == null){
            appointmentIds = new ArrayList<>();
        }
        appointmentIds.add(appointment.getId());
        user.setAppointmentIds(appointmentIds);
        userRepository.save(user);
    }

    public void notifyNutritionist(int patientId, int nutritionistId, Date startDate, String type){
        UserEntity nutritionist = userRepository.findById(nutritionistId).orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity patient = userRepository.findById(patientId).orElseThrow(() -> new RuntimeException("User not found"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
        String formattedDate = sdf.format(startDate);

        String message = String.format(
                "Patient %s %s booked %s starting at %s",
                patient.getName(),
                patient.getLastname(),
                type.toLowerCase(),
                formattedDate
        );

        senderService.sendEmail(nutritionist.getEmail(), "", message);
    }

    public AppointmentInfoDto getAppointmentInfo(int id){
        Appointment a = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity nutritionist = userRepository.findById(a.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));

        AppointmentInfoDto dto = new AppointmentInfoDto();
        dto.setAppointmentDate(a.getDateAndTime());
        dto.setNutritionistFullName(nutritionist.getName() + ' ' + nutritionist.getLastname());
        dto.setType(a.getType());
        return dto;
    }

    @Scheduled(cron = "0 0 8 * * ?") // svaki dan u 8:00
    public void notifyAppointmentIn2Days() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 2); // dodaj 2 dana
        Date twoDaysLater = cal.getTime();

        List<Appointment> appointments = appointmentRepository.findAppointmentsInNext2Days(now, twoDaysLater);

        // Map za praćenje poslatih mejlova po dateBooked
        Set<Date> processedDateBooked = new HashSet<>();

        for (Appointment a : appointments) {
            // ako je već poslat mejl za ovaj dateBooked, preskoči
            if (processedDateBooked.contains(a.getDateBooked())) continue;

            UserEntity nutritionist = userRepository.findById(a.getNutritionistId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserEntity patient = userRepository.findById(a.getPatientId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
            String formattedDate = sdf.format(a.getDateAndTime());

            String message = String.format(
                    "REMINDER! Patient %s %s booked %s starting at %s, nutritionist: %s %s",
                    patient.getName(),
                    patient.getLastname(),
                    a.getType().toLowerCase(),
                    formattedDate,
                    nutritionist.getName(),
                    nutritionist.getLastname()
            );

            senderService.sendEmail(nutritionist.getEmail(), "REMINDER", message);

            // označimo dateBooked kao obrađen
            processedDateBooked.add(a.getDateBooked());
        }
    }

    public void createDailyAppointments(String stringDate, int nutritionistId){
        Date date = new Date(stringDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endOfDay = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date currentTime = cal.getTime();

        List<Date> existingAppointments = appointmentRepository.getNutritionistAppointmentsTimeByDate(date, endOfDay, nutritionistId);

        while (cal.get(Calendar.HOUR_OF_DAY) < 15 ||
                (cal.get(Calendar.HOUR_OF_DAY) == 15 && cal.get(Calendar.MINUTE) <= 40)) {

            boolean exists = false;
            for (Date existing : existingAppointments) {
                if (existing.getTime() == currentTime.getTime()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                CreateAppointmentDto dto = new CreateAppointmentDto(currentTime, 20, nutritionistId);
                createAppointment(dto);
            }

            cal.add(Calendar.MINUTE, 20);
            currentTime = cal.getTime();
        }
    }

    public List<NutritionistAppointmentDto> getAvailableAppointmentsByNutritionistId(int id){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(id);
        List<NutritionistAppointmentDto> dtos = new ArrayList<>();

        for(Appointment a : appointments){
            if(a.isAvailable()) {
                UserEntity user = userRepository.findById(a.getPatientId()).orElse(null);
                NutritionistAppointmentDto dto = new NutritionistAppointmentDto(a.getId(), a.getDateAndTime(), a.getDuration(), "");
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
