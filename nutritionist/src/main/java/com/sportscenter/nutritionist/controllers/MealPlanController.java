package com.sportscenter.nutritionist.controllers;

import com.sportscenter.nutritionist.PDF.PDFGeneratorService;
import com.sportscenter.nutritionist.dto.appointments.NutritionistAppointmentDto;
import com.sportscenter.nutritionist.dto.mealPlan.CreatedMealPlanDto;
import com.sportscenter.nutritionist.dto.mealPlan.NutritionistMealPlanDto;
import com.sportscenter.nutritionist.models.NutritionistMealPlan;
import com.sportscenter.nutritionist.service.MealPlanService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/mealPlan")
public class MealPlanController {
    private MealPlanService mealPlanService;

    @Autowired
    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping("generateMealPlan")
    public ResponseEntity<Void> generateMealPlan(@RequestBody int id) throws MessagingException, IOException {
        mealPlanService.generateMealPlan(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("generateMealPlanHardcoded")
    public ResponseEntity<Void> generateMealPlanHardcoded(@RequestBody int id) throws MessagingException, IOException {
        mealPlanService.generateMealPlanHardcoded(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("createMealPlan")
    public ResponseEntity<Void> createMealPlan(@RequestBody NutritionistMealPlanDto dto) throws MessagingException, IOException {
        mealPlanService.createMealPlan(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getCreatedNutritionistsMealPlans/{id}")
    public ResponseEntity<List<CreatedMealPlanDto>> getCreatedNutritionistsMealPlans(@PathVariable int id){
        return new ResponseEntity<>(mealPlanService.getCreatedNutritionistsMealPlans(id), HttpStatus.OK);
    }
}
