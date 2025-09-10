package com.sportscenter.nutritionist.controllers;

import com.sportscenter.nutritionist.dto.complaint.*;
import com.sportscenter.nutritionist.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaint")
public class ComplaintController {
    private ComplaintService complaintService;

    @Autowired
    public ComplaintController(ComplaintService complaintService){
        this.complaintService = complaintService;
    }

    @PostMapping("createComplaint")
    public ResponseEntity<?> createComplaint(@RequestBody CreateComplaintDto dto){
        complaintService.createComplaint(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getComplaintsByNutritionistId/{id}")
    public ResponseEntity<List<NutritionistComplaintsDto>> getComplaintsByNutritionistId(@PathVariable int id){
        return new ResponseEntity<>(complaintService.getComplaintsByNutritionistId(id), HttpStatus.OK);
    }

    @PostMapping("answerComplaint")
    public ResponseEntity<?> answerComplaint(@RequestBody AnswerComplaintDto dto){
        complaintService.answerComplaint(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getComplaintsAndAnswers")
    public ResponseEntity<List<ComplaintAndAnswerDto>> getComplaintsAndAnswers(){
        return new ResponseEntity<>(complaintService.getComplaintsAndAnswers(), HttpStatus.OK);
    }

    @GetMapping("getAnsweredComplaintsByNutritionistId/{id}")
    public ResponseEntity<List<AnsweredNutritionistComplaintsDto>> getAnsweredComplaintsByNutritionistId(@PathVariable int id){
        return new ResponseEntity<>(complaintService.getAnsweredComplaintsByNutritionistId(id), HttpStatus.OK);
    }

    @GetMapping("getComplaintInfo/{id}")
    public ResponseEntity<ComplaintInfoDto> getComplaintInfo(@PathVariable int id){
        return new ResponseEntity<>(complaintService.getComplaintInfo(id), HttpStatus.OK);
    }
}
