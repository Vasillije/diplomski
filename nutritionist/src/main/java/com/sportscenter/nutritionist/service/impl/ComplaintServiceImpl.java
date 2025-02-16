package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.dto.complaint.AnswerComplaintDto;
import com.sportscenter.nutritionist.dto.complaint.ComplaintAndAnswerDto;
import com.sportscenter.nutritionist.dto.complaint.CreateComplaintDto;
import com.sportscenter.nutritionist.dto.complaint.NutritionistComplaintsDto;
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
import java.util.List;

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

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setHasComplaint(true);
        appointmentRepository.save(appointment);

        UserEntity nutritionist = userRepository.findById(appointment.getNutritionistId()).orElseThrow(() -> new RuntimeException("User not found"));

        emailSender.sendEmail(nutritionist.getEmail(), "complaint on appointment with id "+ appointment.getId(), dto.getContent());
        complaint.setNutritionistId(appointment.getNutritionistId());
        complaint.setPatientId(dto.getPatientId());
        complaintRepository.save(complaint);

    }

    @Override
    public List<NutritionistComplaintsDto> getComplaintsByNutritionistId(int nutritionistId){
        List<Appointment> appointments = appointmentRepository.findAppointmentsByNutritionistId(nutritionistId);
        List<Appointment> validAppointments = new ArrayList<>();
        List<Complaint> complaints = complaintRepository.findAll();
        List<Complaint> nutritionistComplaints = new ArrayList<>();
        List<NutritionistComplaintsDto> complaintDtos = new ArrayList<>();

        for(Appointment a : appointments){
            if(a.isHasComplaint() && !a.isHasComplaintAnswer() && a.getNutritionistId()==nutritionistId){
                validAppointments.add(a);
            }
        }

        for(Appointment a : validAppointments){
            Complaint complaint = complaintRepository.findComplaintByAppointmentId(a.getId());
            nutritionistComplaints.add(complaint);
        }

        for(Complaint c : nutritionistComplaints){
            NutritionistComplaintsDto dto = new NutritionistComplaintsDto();
            dto.setContent(c.getContent());
            dto.setNutritionistId(c.getNutritionistId());
            dto.setId(c.getId());
            dto.setAppointmentId(c.getAppointmentId());
            complaintDtos.add(dto);
        }
        return complaintDtos;
    }

    @Override
    public void answerComplaint(AnswerComplaintDto dto){
        ComplaintAnswer answer = new ComplaintAnswer();
        Complaint complaint = complaintRepository.findById(dto.getComplaintId()).orElseThrow(() -> new RuntimeException("Complaint not found"));
        Appointment appointment = appointmentRepository.findById(complaint.getAppointmentId()).orElseThrow(() -> new RuntimeException("Complaint not found"));

        answer.setComplaintId(dto.getComplaintId());
        answer.setContent(dto.getContent());
        answer.setAppointmentId(appointment.getId());
        answer.setNutritionistId(appointment.getNutritionistId());
        answer.setPatientId(appointment.getPatientId());

        answerRepository.save(answer);

        appointment.setHasComplaintAnswer(true);
        appointmentRepository.save(appointment);
        complaint.setNutritionistId(dto.getNutritionistId());
        complaint.setAnswerId(answer.getId());
        complaintRepository.save(complaint);

        emailSender.sendEmail("", "answer on complaint","Your complaint: "+ complaint.getContent() + " Answer: "+ dto.getContent());
    }

    @Override
    public List<ComplaintAndAnswerDto> getComplaintsAndAnswers(){
        List<Complaint> complaints = complaintRepository.findAll();
        List<ComplaintAndAnswerDto> dtos = new ArrayList<>();
        for(Complaint c : complaints){
            ComplaintAndAnswerDto dto = new ComplaintAndAnswerDto();

            dto.setComplaintId(c.getId());
            dto.setComplaintContent(c.getContent());
            dto.setAnswerId(c.getAnswerId());
            dto.setPatientId(c.getPatientId());
            dto.setAppointmentId(c.getAppointmentId());

            if(c.getAnswerId() != 0) {
                ComplaintAnswer answer = answerRepository.findById(c.getAnswerId()).orElseThrow(() -> new RuntimeException("Answer not found"));
                dto.setNutritionistId(answer.getNutritionistId());
                dto.setAnswerContent(answer.getContent());
            }
            else{
                dto.setNutritionistId(0);
                dto.setAnswerContent("NO ANSWER");
            }
            dtos.add(dto);
        }
        return dtos;
    }
}
