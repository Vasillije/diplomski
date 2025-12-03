package com.sportscenter.nutritionist.controllers;

import com.sportscenter.nutritionist.dto.appointments.*;
import com.sportscenter.nutritionist.models.Appointment;
import com.sportscenter.nutritionist.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    private AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("createAppointment")
    public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentDto dto){
        try{
            return new ResponseEntity<>(appointmentService.createAppointment(dto) ,HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getAvailableAppointments")
    public ResponseEntity<?> getAvailableAppointments() {
        try{
            return new ResponseEntity<>(appointmentService.getAvailableAppointments() ,HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("getAppointmentsByNutritionistId/{id}")
    public ResponseEntity<?> getNutritionistAppointments(@PathVariable int id) {
        try{
            return new ResponseEntity<>(appointmentService.getAppointmentsByNutritionistId(id) ,HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("getAppointmentsByPatientId/{id}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable int id) {
        try{
            return new ResponseEntity<>(appointmentService.getAppointmentsByPatientId(id) ,HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("isAppointmentOverlapping")
    public ResponseEntity<?> isAppointmentOverlapping(@RequestBody CreateAppointmentDto dto){
        return new ResponseEntity<>(appointmentService.isAppointmentOverlapping(dto) ,HttpStatus.OK);
    }

    @PostMapping("cancelAppointmentPatient")
    public  ResponseEntity<?> cancelAppointmentPatient(@RequestBody CancelAppointmentDto dto){
        appointmentService.cancelAppointmentPatient(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("cancelAppointmentNutritionist")
    public ResponseEntity<Void> cancelAppointmentNutritionist(@RequestBody int appointmentId){
        appointmentService.cancelAppointmentNutritionist(appointmentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getPastBookedExaminationsByNutritionistId/{id}")
    public ResponseEntity<List<PastBookedAppointmentsByNutritionistIdDto>> getPastBookedAppointments(@PathVariable int id){
        return new ResponseEntity<>(appointmentService.getPastBookedExaminationsByNutritionistId(id), HttpStatus.OK);
    }

    @GetMapping("getAppointmentsByDate")
    public ResponseEntity<List<AppointmentsByDateDto>> getAppointmentsByDate(
            @RequestParam String date,
            @RequestParam int id
    ) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByDate(date, id), HttpStatus.OK);
    }


    @PostMapping("book")
    public ResponseEntity<Void> book(@RequestBody BookDto dto){
        appointmentService.book(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getAppointmentInfo/{id}")
    public ResponseEntity<AppointmentInfoDto> getAppointmentInfo(@PathVariable int id){
        return new ResponseEntity<>(appointmentService.getAppointmentInfo(id), HttpStatus.OK);
    }

    @PostMapping("notifyAppointmentIn2Days")
    public ResponseEntity<Void> notifyAppointmentIn2Days(){
        appointmentService.notifyAppointmentIn2Days();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("createDailyAppointments")
    public ResponseEntity<Void> createDailyAppointments(@RequestParam String date, @RequestParam int nutritionistId){
        appointmentService.createDailyAppointments(date, nutritionistId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getAvailableAppointmentsByNutritionistId/{id}")
    public ResponseEntity<List<NutritionistAppointmentDto>> getAvailableAppointmentsByNutritionistId(@PathVariable int id){
        return new ResponseEntity<>(appointmentService.getAvailableAppointmentsByNutritionistId(id), HttpStatus.OK);
    }

}
