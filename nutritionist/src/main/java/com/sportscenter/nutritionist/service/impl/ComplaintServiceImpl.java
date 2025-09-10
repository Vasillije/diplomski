package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.dto.complaint.*;
import com.sportscenter.nutritionist.email.EmailSenderService;
import com.sportscenter.nutritionist.models.Appointment;
import com.sportscenter.nutritionist.models.Complaint;
import com.sportscenter.nutritionist.models.ComplaintAnswer;
import com.sportscenter.nutritionist.models.UserEntity;
import com.sportscenter.nutritionist.repository.AnswerRepository;
import com.sportscenter.nutritionist.repository.AppointmentRepository;
import com.sportscenter.nutritionist.repository.ComplaintRepository;
import com.sportscenter.nutritionist.repository.UserRepository;
import com.sportscenter.nutritionist.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private ComplaintRepository complaintRepository;
    private AppointmentRepository appointmentRepository;
    private EmailSenderService emailSender;
    private UserRepository userRepository;
    private AnswerRepository answerRepository;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, AppointmentRepository appointmentRepository, EmailSenderService emailSender,
                                UserRepository userRepository, AnswerRepository answerRepository) {
        this.complaintRepository = complaintRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public void createComplaint(CreateComplaintDto dto) {
        Complaint complaint = new Complaint();
        complaint.setAppointmentId(dto.getAppointmentId());
        complaint.setContent(dto.getContent());
        complaint.setAnswerId(0);
        complaint.setDateAndTime(dto.getDate());

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setHasComplaint(true);
        appointmentRepository.save(appointment);

        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));

        emailSender.sendEmail(nutritionist.getEmail(), "complaint on appointment with id "+ appointment.getId(), dto.getContent());
        complaint.setNutritionistId(appointment.getNutritionistId());
        complaint.setPatientId(dto.getPatientId());
        complaintRepository.save(complaint);

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateBooked(appointment.getDateBooked());

        if(!appointments.isEmpty()){
            for(Appointment a : appointments){
                if(a.getNutritionistId() == appointment.getNutritionistId()){
                    a.setHasComplaint(true);
                    appointmentRepository.save(a);
                }
            }
        }

    }

    @Override
    public List<NutritionistComplaintsDto> getComplaintsByNutritionistId(int nutritionistId){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(nutritionistId);
        List<Appointment> validAppointments = new ArrayList<>();
        List<Complaint> complaints = complaintRepository.findAll();
        List<Complaint> nutritionistComplaints = new ArrayList<>();
        List<NutritionistComplaintsDto> complaintDtos = new ArrayList<>();
        List<UserEntity> patients = userRepository.findAllPatients();

        for(Appointment a : appointments){
            if(a.isHasComplaint() && !a.isHasComplaintAnswer() && a.getNutritionistId()==nutritionistId){
                validAppointments.add(a);
            }
        }

        for(Appointment a : validAppointments){
            NutritionistComplaintsDto dto = new NutritionistComplaintsDto();
            Complaint c = complaintRepository.findComplaintByAppointmentId(a.getId());

            if(c == null){
                continue;
            }

            dto.setContent(c.getContent());
            dto.setNutritionistId(c.getNutritionistId());
            dto.setId(c.getId());
            dto.setAppointmentId(c.getAppointmentId());
            if(c.getPatientId() != 0){
                for(UserEntity patient : patients){
                    if(patient.getId() == c.getPatientId()){
                        dto.setPatientsFullName(patient.getName() + ' ' + patient.getLastname());
                    }
                }
            }

            if(c.getDateAndTime() != null){
                dto.setComplaintDate(c.getDateAndTime());
            }

            dto.setAppointmentDate(a.getDateAndTime());

            complaintDtos.add(dto);
        }

        return complaintDtos;
    }

    @Override
    public void answerComplaint(AnswerComplaintDto dto){
        ComplaintAnswer answer = new ComplaintAnswer();
        Complaint complaint = complaintRepository.findById(dto.getComplaintId()).orElseThrow(() -> new RuntimeException("Complaint not found"));
        Appointment appointment = appointmentRepository.findById(complaint.getAppointmentId()).orElseThrow(() -> new RuntimeException("Complaint not found"));
        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateBooked(appointment.getDateBooked());

        answer.setComplaintId(dto.getComplaintId());
        answer.setContent(dto.getContent());
        answer.setAppointmentId(appointment.getId());
        answer.setNutritionistId(appointment.getNutritionistId());
        answer.setPatientId(appointment.getPatientId());
        answer.setDateAndTime(dto.getDate());

        answerRepository.save(answer);

        if(!appointments.isEmpty()){
            for(Appointment a : appointments){
                a.setHasComplaintAnswer(true);
                appointmentRepository.save(a);
            }
        }

        appointment.setHasComplaintAnswer(true);
        appointmentRepository.save(appointment);
        complaint.setNutritionistId(dto.getNutritionistId());
        complaint.setAnswerId(answer.getId());
        complaintRepository.save(complaint);

        emailSender.sendEmail("", "answer on complaint","Your complaint: "+ complaint.getContent() + " Answer: "+ dto.getContent());
    }


    @Override
    public List<ComplaintAndAnswerDto> getComplaintsAndAnswers() {
        List<Complaint> complaints = complaintRepository.findAll();
        List<ComplaintAnswer> answers = answerRepository.findAll();
        List<ComplaintAndAnswerDto> dtos = new ArrayList<>();
        List<UserEntity> users = userRepository.findAll();
        List<Appointment> appointments = appointmentRepository.findAll();

        Map<Integer, ComplaintAnswer> answerMap = new HashMap<>();

        for (ComplaintAnswer a : answers) {
            answerMap.put(a.getId(), a);
        }

        Map<Integer, UserEntity> userMap = new HashMap<>();

        for (UserEntity user : users) {
            userMap.put(user.getId(), user);
        }

        Map<Integer, Appointment> appointmentMap = new HashMap<>();

        for (Appointment appointment : appointments) {
            appointmentMap.put(appointment.getId(), appointment);
        }

        for (Complaint c : complaints) {
            ComplaintAndAnswerDto dto = new ComplaintAndAnswerDto();
            dto.setComplaintId(c.getId());
            dto.setComplaintContent(c.getContent());
            dto.setAnswerId(c.getAnswerId());
            dto.setPatientId(c.getPatientId());
            dto.setAppointmentId(c.getAppointmentId());
            dto.setComplaintDate(c.getDateAndTime());

            if(c.getAnswerId() != 0) {
                ComplaintAnswer a = answerMap.get(c.getAnswerId());
                dto.setNutritionistId(a.getNutritionistId());
                dto.setAnswerContent(a.getContent());
                dto.setAnswerDate(a.getDateAndTime());
            }
            else {
                dto.setNutritionistId(0);
                dto.setAnswerContent("NO ANSWER");
            }

            UserEntity patient = userMap.get(c.getPatientId());
            dto.setPatientFullName(patient.getName() + " " + patient.getLastname());
            UserEntity nutritionist = userMap.get(c.getNutritionistId());
            dto.setNutritionistFullName(nutritionist.getName() + " " + nutritionist.getLastname());
            Appointment appointment = appointmentMap.get(c.getAppointmentId());
            dto.setAppointmentDate(appointment.getDateAndTime());

            dtos.add(dto);
        }
        return dtos;
    }

    public List<AnsweredNutritionistComplaintsDto> getAnsweredComplaintsByNutritionistId(int nutritionistId){
        List<Appointment> allAppointments = appointmentRepository.findAppointmentsByNutritionistId(nutritionistId);
        List<Appointment> validAppointments = new ArrayList<>();
        List<UserEntity> patients = userRepository.findAllPatients();
        List<AnsweredNutritionistComplaintsDto> dtos = new ArrayList<>();

        for(Appointment a : allAppointments){
            if(a.isHasComplaintAnswer()){
                validAppointments.add(a);
            }
        }

        for(Appointment a : validAppointments){
            AnsweredNutritionistComplaintsDto dto = new AnsweredNutritionistComplaintsDto();
            Complaint c = complaintRepository.findComplaintByAppointmentId(a.getId());
            ComplaintAnswer ca = answerRepository.findComplaintAnswerByAppointmentId(a.getId());

            if(ca == null){
                continue;
            }

            dto.setAnswerContent(ca.getContent());
            dto.setNutritionistId(c.getNutritionistId());
            dto.setContent(c.getContent());
            dto.setComplaintDate(c.getDateAndTime());
            for(UserEntity patient : patients){
                if(c.getPatientId() == patient.getId()){
                    dto.setPatientsFullName(patient.getName() + ' ' + patient.getLastname());
                }
            }
            dto.setAppointmentId(a.getId());
            dto.setAnswerDate(ca.getDateAndTime());
            dto.setAppointmentDate(a.getDateAndTime());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public ComplaintInfoDto getComplaintInfo(int id){
        Complaint complaint = complaintRepository.findById(id).orElseThrow(() -> new RuntimeException("Complaint not found"));
        Appointment appointment = appointmentRepository.findById(complaint.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity patient = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));

        ComplaintInfoDto dto = new ComplaintInfoDto();
        dto.setId(complaint.getId());
        dto.setComplaintDate(complaint.getDateAndTime());
        dto.setAppointmentDate(appointment.getDateAndTime());
        dto.setContent(complaint.getContent());
        dto.setPatientFullName(patient.getName() + " " + patient.getLastname());

        return dto;
    }

}
