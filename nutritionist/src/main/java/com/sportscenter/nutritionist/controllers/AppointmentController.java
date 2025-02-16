package com.sportscenter.nutritionist.controllers;

import com.sportscenter.nutritionist.dto.appointments.BookAppointmentDto;
import com.sportscenter.nutritionist.dto.appointments.CreateAppointmentDto;
import com.sportscenter.nutritionist.dto.appointments.PastBookedAppointmentsByNutritionistIdDto;
import com.sportscenter.nutritionist.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("bookAppointment")
    public ResponseEntity<Void> bookAppointment(@RequestBody BookAppointmentDto dto){
        try{
            appointmentService.bookAppointment(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public  ResponseEntity<?> cancelAppointmentPatient(@RequestBody int appointmentId){
        appointmentService.cancelAppointmentPatient(appointmentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("cancelAppointmentNutritionist")
    public ResponseEntity<Void> cancelAppointmentNutritionist(@RequestBody int appointmentId){
        appointmentService.cancelAppointmentNutritionist(appointmentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getPastBookedAppointmentsByNutritionistId/{id}")
    public ResponseEntity<List<PastBookedAppointmentsByNutritionistIdDto>> getPastBookedAppointments(@PathVariable int id){
        return new ResponseEntity<>(appointmentService.getPastBookedAppointmentsByNutritionistId(id), HttpStatus.OK);
    }

}
