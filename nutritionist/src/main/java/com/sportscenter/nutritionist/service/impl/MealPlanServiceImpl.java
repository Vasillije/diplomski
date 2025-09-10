package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.PDF.PDFGeneratorService;
import com.sportscenter.nutritionist.dto.mealPlan.NutritionistMealPlanDto;
import com.sportscenter.nutritionist.email.EmailSenderService;
import com.sportscenter.nutritionist.enums.Gender;
import com.sportscenter.nutritionist.enums.Goal;
import com.sportscenter.nutritionist.enums.MealType;
import com.sportscenter.nutritionist.enums.PhysicalActivity;
import com.sportscenter.nutritionist.models.*;
import com.sportscenter.nutritionist.repository.*;
import com.sportscenter.nutritionist.service.MealPlanService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class MealPlanServiceImpl implements MealPlanService {
    private FoodRepository foodRepository;
    private NutritionPlanRepository nutritionPlanRepository;
    private UserNutritionPlanRepository userNutritionPlanRepository;
    private UserRepository userRepository;
    private EmailSenderService emailSenderService;
    private PDFGeneratorService pdfGeneratorService;
    private NutritionistMealPlanRepository nutritionistMealPlanRepository;
    private AppointmentRepository appointmentRepository;

    @Autowired
    public MealPlanServiceImpl(FoodRepository foodRepository, NutritionPlanRepository nutritionPlanRepository,
                           UserNutritionPlanRepository userNutritionPlanRepository, UserRepository userRepository,
                           PDFGeneratorService pdfGeneratorService, EmailSenderService emailSenderService,
                               NutritionistMealPlanRepository nutritionistMealPlanRepository,
                               AppointmentRepository appointmentRepository) {
        this.foodRepository = foodRepository;
        this.nutritionPlanRepository = nutritionPlanRepository;
        this.userNutritionPlanRepository =userNutritionPlanRepository;
        this.userRepository = userRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.emailSenderService = emailSenderService;
        this.nutritionistMealPlanRepository = nutritionistMealPlanRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public double calculateBMI(UserEntity user){
        double bmi = 0;
        if(user.getGender() == Gender.MALE){
            bmi = 88.4 + 13.4*user.getWeight() + 4.8 * user.getHeight() - 5.68 * user.getAge();
        } else if(user.getGender() == Gender.FEMALE){
            bmi = 447.6 + 9.25*user.getWeight() + 3.10*user.getHeight() - 4.33*user.getAge();
        }
        return bmi;
    }

    public double calculateCalories(UserEntity user){
        double kcal = 0;
        double bmi = calculateBMI(user);
        if (bmi == 0){
            throw new IllegalArgumentException("ERROR");
        }
        if(user.getPhysicalActivity() == PhysicalActivity.NONE){
            kcal = bmi + 100;
        } else if(user.getPhysicalActivity() == PhysicalActivity.LOW) {
            kcal = bmi + 300;
        } else if(user.getPhysicalActivity() == PhysicalActivity.MEDIUM) {
            kcal = bmi + 500;
        } else if(user.getPhysicalActivity() == PhysicalActivity.HIGH) {
            kcal = bmi + 700;
        } else {
            throw new IllegalArgumentException("ERROR");
        }
        return kcal;
    }

    @Override
    public void generateMealPlan(int userId) throws MessagingException, IOException {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<UserNutritionPlan> userNutritionPlans = userNutritionPlanRepository.findUserNutritionPlanByUser(user);

        if(!userNutritionPlans.isEmpty()){
            userNutritionPlanRepository.deleteAll(userNutritionPlans);
        }

        double calories = calculateCalories(user);

        if(user.getGoal() == Goal.LOOSE_WEIGHT){
            this.generateMealPlanLooseWeight(user, calories);
        }
        else if(user.getGoal() == Goal.HEALTH){
            this.generateMealPlanHealth(user, calories);
        }
        else if(user.getGoal() == Goal.GAIN_WEIGHT){
            this.generateMealPlanGainWeight(user, calories);
        }

    }

    public double calculateGramsPerMeal(double calories, int foodId){
        double grams;
        Food food = foodRepository.findById(foodId).orElseThrow(() -> new RuntimeException("Food not found"));
        grams = (100 * calories) / food.getKcal();
        return grams;
    }

    public void generateMealPlanLooseWeight(UserEntity user, double basicCalories) throws MessagingException, IOException {
        double calories = basicCalories - 400;
        double breakfastCalories = 0.3 * calories;
        double lunchCalories = 0.4 * calories;
        double dinnerCalories = 0.3 * calories;

        List<NutritionPlan> meals = nutritionPlanRepository.findNutritionPlansByMenuNumber(1);

        for(NutritionPlan n : meals){
            if(n.getDay() == 1){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(1).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.6,
                            calculateGramsPerMeal(breakfastCalories * 0.6, 1));
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(2).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.25,
                            calculateGramsPerMeal(breakfastCalories * 0.25, 2));
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(3).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.15,
                            calculateGramsPerMeal(breakfastCalories * 0.15, 3));
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(4).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            lunchCalories * 0.7, calculateGramsPerMeal(lunchCalories * 0.7, 4)
                    );
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(5).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            breakfastCalories * 0.3, calculateGramsPerMeal(breakfastCalories * 0.3, 5)
                    );
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan( user,
                            nutritionPlanRepository.findById(6).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.8, 6), dinnerCalories * 0.8);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan( user,
                            nutritionPlanRepository.findById(7).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 7), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);
                }
            } else if(n.getDay() == 2) {
                if (n.getMealType() == MealType.BREAKFAST) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(8).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 8), breakfastCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(9).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 9), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(10).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 10), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if (n.getMealType() == MealType.LUNCH) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(11).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 11), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(12).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 19), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);
                } else if (n.getMealType() == MealType.DINNER) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(13).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories, 12), dinnerCalories);
                    userNutritionPlanRepository.save(userPlan1);
                }
            }
            else if(n.getDay() == 3){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(14).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories, 50), breakfastCalories);
                    userNutritionPlanRepository.save(userPlan1);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(15).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories, 13), lunchCalories);
                    userNutritionPlanRepository.save(userPlan1);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(16).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 14), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(17).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.4, 15), dinnerCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(18).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.4, 10), dinnerCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 4){
                if (n.getMealType() == MealType.BREAKFAST) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(19).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.7, 16), breakfastCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(20).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 17), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);
                } else if (n.getMealType() == MealType.LUNCH) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(21).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.575, 18), lunchCalories * 0.575);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(22).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.4, 19), lunchCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(23).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.025, 20), lunchCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan3);
                } else if (n.getMealType() == MealType.DINNER) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(24).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 21), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(25).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 14), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(26).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.55, 8), dinnerCalories * 0.55);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(27).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.025, 24), dinnerCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(28).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.025, 23), dinnerCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan5);
                }
            } else if(n.getDay() == 5){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(29).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.8, 25), breakfastCalories * 0.8);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(30).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 17), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(31).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.45, 11), lunchCalories * 0.45);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(32).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.45, 26), lunchCalories * 0.45);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(33).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.034, 27), lunchCalories * 0.034);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(34).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.033, 24), lunchCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(35).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.033, 23), lunchCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan5);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(36).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 28), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(37).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 16), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(38).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 18), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(39).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.1, 14), dinnerCalories * 0.1);
                    userNutritionPlanRepository.save(userPlan4);
                }
            } else if(n.getDay() == 6){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(40).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.8, 29), breakfastCalories * 0.8);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(41).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 3), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(42).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.35, 28), lunchCalories * 0.35);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(43).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.55, 11), lunchCalories * 0.55);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(44).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.1, 14), lunchCalories * 0.1);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(45).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 6), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(46).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.05, 30), dinnerCalories * 0.05);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(47).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.25 , 10), dinnerCalories * 0.25);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 7){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(48).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 31), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(49).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 15), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(50).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.425, 46), lunchCalories * 0.425);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(51).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 10), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(52).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.125, 23), lunchCalories * 0.125);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(53).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.125, 27), lunchCalories * 0.125);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(54).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.35, 18), lunchCalories * 0.35);
                    userNutritionPlanRepository.save(userPlan5);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(55).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.28, 19), dinnerCalories * 0.28);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(56).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 11), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(57).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.02, 51), dinnerCalories * 0.02);
                    userNutritionPlanRepository.save(userPlan3);
                }
            }
        }
        sendMailPdf(user);
    }

    public void generateMealPlanHealth(UserEntity user, double basicCalories) throws MessagingException, IOException {
        double breakfastCalories = 0.3 * basicCalories;
        double lunchCalories = 0.4 * basicCalories;
        double dinnerCalories = 0.3 * basicCalories;

        List<NutritionPlan> meals = nutritionPlanRepository.findNutritionPlansByMenuNumber(1);

        for(NutritionPlan n : meals){
            if(n.getDay() == 1){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(1).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.6,
                            calculateGramsPerMeal(breakfastCalories * 0.6, 1));
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(2).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.25,
                            calculateGramsPerMeal(breakfastCalories * 0.25, 2));
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(3).orElseThrow(() -> new RuntimeException("Nutrition plan not found")), breakfastCalories * 0.15,
                            calculateGramsPerMeal(breakfastCalories * 0.15, 3));
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(4).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            lunchCalories * 0.8, calculateGramsPerMeal(lunchCalories * 0.8, 4)
                    );
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(5).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            breakfastCalories * 0.2, calculateGramsPerMeal(breakfastCalories * 0.2, 5)
                    );
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan( user,
                            nutritionPlanRepository.findById(6).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 6), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan( user,
                            nutritionPlanRepository.findById(7).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 7), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);
                }
            } else if(n.getDay() == 2) {
                if (n.getMealType() == MealType.BREAKFAST) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(8).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 8), breakfastCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(9).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 9), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(10).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 10), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if (n.getMealType() == MealType.LUNCH) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(11).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 11), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(12).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 19), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);
                } else if (n.getMealType() == MealType.DINNER) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(13).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories, 12), dinnerCalories);
                    userNutritionPlanRepository.save(userPlan1);
                }
            }
            else if(n.getDay() == 3){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(14).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories, 50), breakfastCalories);
                    userNutritionPlanRepository.save(userPlan1);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(15).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories, 13), lunchCalories);
                    userNutritionPlanRepository.save(userPlan1);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(16).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 14), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(17).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.4, 15), dinnerCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(18).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.4, 10), dinnerCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 4){
                if (n.getMealType() == MealType.BREAKFAST) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(19).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.7, 16), breakfastCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(20).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 17), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);
                } else if (n.getMealType() == MealType.LUNCH) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(21).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.575, 18), lunchCalories * 0.575);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(22).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.4, 19), lunchCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(23).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.025, 20), lunchCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan3);
                } else if (n.getMealType() == MealType.DINNER) {
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(24).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 21), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(25).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 14), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(26).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.55, 8), dinnerCalories * 0.55);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(27).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.025, 24), dinnerCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(28).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.025, 23), dinnerCalories * 0.025);
                    userNutritionPlanRepository.save(userPlan5);
                }
            } else if(n.getDay() == 5){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(29).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.8, 25), breakfastCalories * 0.8);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(30).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 17), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(31).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.45, 11), lunchCalories * 0.45);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(32).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.45, 26), lunchCalories * 0.45);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(33).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.034, 27), lunchCalories * 0.034);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(34).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.033, 24), lunchCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(35).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.033, 23), lunchCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan5);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(36).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 28), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(37).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 16), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(38).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 18), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(39).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.1, 14), dinnerCalories * 0.1);
                    userNutritionPlanRepository.save(userPlan4);
                }
            } else if(n.getDay() == 6){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(40).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.8, 29), breakfastCalories * 0.8);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(41).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 3), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(42).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.35, 28), lunchCalories * 0.35);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(43).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.55, 11), lunchCalories * 0.55);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(44).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.1, 14), lunchCalories * 0.1);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(45).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 6), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(46).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.28, 30), dinnerCalories * 0.28);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(47).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.02, 10), dinnerCalories * 0.02);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 7){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(48).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 31), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(49).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 15), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(50).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.425, 46), lunchCalories * 0.425);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(51).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 10), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(52).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.125, 23), lunchCalories * 0.125);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(53).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.125, 27), lunchCalories * 0.125);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(54).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.35, 18), lunchCalories * 0.35);
                    userNutritionPlanRepository.save(userPlan5);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(55).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.28, 19), dinnerCalories * 0.28);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(56).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 11), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(57).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.02, 51), dinnerCalories * 0.02);
                    userNutritionPlanRepository.save(userPlan3);
                }
            }
        }
        sendMailPdf(user);
    }

    public void generateMealPlanGainWeight(UserEntity user, double basicCalories) throws MessagingException, IOException {
        double calories = basicCalories + 400;
        double breakfastCalories = 0.3 * calories;
        double lunchCalories = 0.4 * calories;
        double dinnerCalories = 0.3 * calories;

        List<NutritionPlan> meals = nutritionPlanRepository.findNutritionPlansByMenuNumber(2);

        for(NutritionPlan n : meals){
            if(n.getDay() == 1){
                if(n.getMealType() == MealType.BREAKFAST){

                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(58).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.4, 1), breakfastCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(59).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 3), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(60).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 34), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(61).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 33), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan4);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(62).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 11), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(63).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 19), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(64).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.5, 16), dinnerCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(65).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 10), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(66).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 46), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 2){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(67).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 10), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(68).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 35), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(69).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 17), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(70).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.6, 36), lunchCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(71).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 37), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(72).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 52), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(73).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.5, 18), dinnerCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(74).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.43, 7), dinnerCalories * 0.43);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(75).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.035, 30), dinnerCalories * 0.035);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(76).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.035, 23), dinnerCalories * 0.035);
                    userNutritionPlanRepository.save(userPlan4);
                }
            } else if(n.getDay() == 3){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(77).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 33), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(78).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 1), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(79).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 3), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(80).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 34), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(81).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 8), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan5);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(82).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.34, 39), lunchCalories * 0.34);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(83).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.33, 40), lunchCalories * 0.33);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(84).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.33, 41), lunchCalories * 0.33);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(85).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.6, 18), dinnerCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(86).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.37, 37), dinnerCalories * 0.37);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(87).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.03, 20), dinnerCalories * 0.03);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 4){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(88).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.4, 42), breakfastCalories * 0.4);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(89).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 35), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(90).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.1, 3), breakfastCalories * 0.1);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(91).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 33), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan4);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(92).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.6, 43), lunchCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(93).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.25, 19), lunchCalories * 0.25);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(94).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.15, 52), lunchCalories * 0.15);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(95).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.7, 16), dinnerCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(96).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 10), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);
                }
            } else if(n.getDay() == 5){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(97).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 53), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(98).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 8), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(99).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 33), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(100).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.3, 34), breakfastCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan4);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(101).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.7, 11), lunchCalories * 0.7);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(102).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.3, 45), lunchCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(103).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.65, 12), dinnerCalories * 0.65);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(104).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.35, 10), dinnerCalories * 0.35);
                    userNutritionPlanRepository.save(userPlan2);
                }
            } else if(n.getDay() == 6){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(105).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.6, 16), breakfastCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(106).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 10), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(107).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.2, 46), breakfastCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(108).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.6, 39), lunchCalories * 0.6);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(109).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 21), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(110).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 47), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(111).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.5, 16), dinnerCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(112).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 37), dinnerCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(113).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.2, 17), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                }
            } else if(n.getDay() == 7){
                if(n.getMealType() == MealType.BREAKFAST){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(114).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 48), breakfastCalories * 0.25);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(115).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.25, 49), breakfastCalories * 0.25);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(116).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(breakfastCalories * 0.5, 41), breakfastCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.LUNCH){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(117).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.5, 11), lunchCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(118).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.3, 19), lunchCalories * 0.3);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(119).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(lunchCalories * 0.2, 52), lunchCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan3);
                } else if(n.getMealType() == MealType.DINNER){
                    UserNutritionPlan userPlan1 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(111).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.5, 18), dinnerCalories * 0.5);
                    userNutritionPlanRepository.save(userPlan1);

                    UserNutritionPlan userPlan2 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(112).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.033, 30), dinnerCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan2);

                    UserNutritionPlan userPlan3 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(113).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.034, 24), dinnerCalories * 0.034);
                    userNutritionPlanRepository.save(userPlan3);

                    UserNutritionPlan userPlan4 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(112).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.033, 23), dinnerCalories * 0.033);
                    userNutritionPlanRepository.save(userPlan4);

                    UserNutritionPlan userPlan5 = new UserNutritionPlan(user,
                            nutritionPlanRepository.findById(113).orElseThrow(() -> new RuntimeException("Nutrition plan not found")),
                            calculateGramsPerMeal(dinnerCalories * 0.3, 10), dinnerCalories * 0.2);
                    userNutritionPlanRepository.save(userPlan5);
                }
            }
            sendMailPdf(user);
        }
    }

    public void sendMailPdf(UserEntity user) throws MessagingException, IOException {
        List<UserNutritionPlan> meals = userNutritionPlanRepository.findUserNutritionPlanByUser(user);
        String mondayText = "\n";
        String mondayTextBreakfast = "\nBreakfast: \n";
        String mondayTextLunch = "\nLunch: \n";
        String mondayTextDinner = "\nDinner: \n";
        String tuesdayText = "\n";
        String tuesdayTextBreakfast = "\nBreakfast: \n";
        String tuesdayTextLunch = "\nLunch: \n";
        String tuesdayTextDinner = "\nDinner: \n";
        String wednesdayText = "\n";
        String wednesdayTextBreakfast = "\nBreakfast: \n";
        String wednesdayTextLunch = "\nLunch: \n";
        String wednesdayTextDinner = "\nDinner: \n";
        String thursdayText = "\n";
        String thursdayTextBreakfast = "\nBreakfast: \n";
        String thursdayTextLunch = "\nLunch: \n";
        String thursdayTextDinner = "\nDinner: \n";
        String fridayText = "\n";
        String fridayTextBreakfast = "\nBreakfast: \n";
        String fridayTextLunch = "\nLunch: \n";
        String fridayTextDinner = "\nDinner: \n";
        String saturdayText = "\n";
        String saturdayTextBreakfast = "\nBreakfast: \n";
        String saturdayTextLunch = "\nLunch: \n";
        String saturdayTextDinner = "\nDinner: \n";
        String sundayText = "\n";
        String sundayTextBreakfast = "\nBreakfast: \n";
        String sundayTextLunch = "\nLunch: \n";
        String sundayTextDinner = "\nDinner: \n";
        for(UserNutritionPlan n : meals){
            NutritionPlan plan = nutritionPlanRepository.findById(n.getNutritionPlanId()).orElseThrow(() -> new RuntimeException("Nutrition plan not found"));
            if(plan.getDay() == 1) {
                if(plan.getMealType() == MealType.BREAKFAST){
                    mondayTextBreakfast = mondayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    mondayTextLunch = mondayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    mondayTextDinner = mondayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                mondayText = mondayTextBreakfast + mondayTextLunch + mondayTextDinner;
            } else if(plan.getDay() == 2){
                if(plan.getMealType() == MealType.BREAKFAST){
                    tuesdayTextBreakfast = tuesdayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    tuesdayTextLunch = tuesdayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    tuesdayTextDinner = tuesdayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                tuesdayText = tuesdayTextBreakfast + tuesdayTextLunch + tuesdayTextDinner;
            } else if(plan.getDay() == 3){
                if(plan.getMealType() == MealType.BREAKFAST){
                    wednesdayTextBreakfast = wednesdayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    wednesdayTextLunch = wednesdayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    wednesdayTextDinner = wednesdayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                wednesdayText = wednesdayTextBreakfast + wednesdayTextLunch + wednesdayTextDinner;
            }
            else if(plan.getDay() == 4){
                if(plan.getMealType() == MealType.BREAKFAST){
                    thursdayTextBreakfast = thursdayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    thursdayTextLunch = thursdayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    thursdayTextDinner = thursdayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                thursdayText = thursdayTextBreakfast + thursdayTextLunch + thursdayTextDinner;
            } else if(plan.getDay() == 5){
                if(plan.getMealType() == MealType.BREAKFAST){
                    fridayTextBreakfast = fridayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    fridayTextLunch = fridayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    fridayTextDinner = fridayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                fridayText = fridayTextBreakfast + fridayTextLunch + fridayTextDinner;
            }else if(plan.getDay() == 6){
                if(plan.getMealType() == MealType.BREAKFAST){
                    saturdayTextBreakfast = saturdayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    saturdayTextLunch = saturdayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    saturdayTextDinner = saturdayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                saturdayText = saturdayTextBreakfast + saturdayTextLunch + saturdayTextDinner;
            } else if(plan.getDay() == 7){
                if(plan.getMealType() == MealType.BREAKFAST){
                    sundayTextBreakfast = sundayTextBreakfast + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.LUNCH){
                    sundayTextLunch = sundayTextLunch + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                } else if(plan.getMealType() == MealType.DINNER){
                    sundayTextDinner = sundayTextDinner + plan.getFood().getName() + " " + (int)n.getGrams() + " grams ";
                }
                sundayText = sundayTextBreakfast + sundayTextLunch + sundayTextDinner;
            }

        }

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PDFGeneratorService pdfService = new PDFGeneratorService();
        pdfService.export(pdfStream, mondayText, tuesdayText, wednesdayText, thursdayText, fridayText, saturdayText, sundayText);
        System.out.println("PDF size: " + pdfStream.size());
        emailSenderService.sendEmailWithAttachment(user.getEmail(), "Meal plan", "Your meal plan.", pdfStream);
    }

    public void createMealPlan(NutritionistMealPlanDto dto) throws IOException, MessagingException {
        NutritionistMealPlan plan = new NutritionistMealPlan();
        plan.setMealPlan(dto.getContent());
        plan.setNutritionistId(dto.getNutritionistId());
        plan.setPatientId(dto.getPatientId());
        nutritionistMealPlanRepository.save(plan);

        Appointment a = appointmentRepository.findById(dto.getAppointmentId()).orElseThrow(() -> new RuntimeException("Appointment plan not found"));
        a.setHasMealPlan(true);
        appointmentRepository.save(a);

        List<Appointment> appointments = appointmentRepository.findAppointmentsByDateBooked(a.getDateBooked());

        for(Appointment appointment : appointments){
            if(a.getNutritionistId() == appointment.getNutritionistId()){
                appointment.setHasMealPlan(true);
                appointmentRepository.save(appointment);
            }
        }

        String text = dto.getContent();

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PDFGeneratorService pdfService = new PDFGeneratorService();
        pdfService.export1(pdfStream, text);
        System.out.println("PDF size: " + pdfStream.size());
        emailSenderService.sendEmailWithAttachment("nebitno", "Meal plan", "Your meal plan.", pdfStream);
    }

}
