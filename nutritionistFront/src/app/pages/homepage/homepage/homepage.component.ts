import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../../login/service/login.service';
import { UserProfileService } from '../../user-profile/services/user-profile.service';
import { UserDto } from '../../user-profile/interfaces/UserDto';
import { MealPlanService } from '../../meal-plan/service/meal-plan.service';

@Component({
  selector: 'app-homepage',
  standalone: false,
  
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {

  constructor(private router : Router, public loginService : LoginService, private userProfileService : UserProfileService,
              private mealPlanService : MealPlanService){}

  userProfile(){
    this.router.navigate(['user-profile']);
  }

  nutritionistAppointmentView() {
    this.router.navigate(['nutritionist-appointment-view']);
  }

  patientAppointmentView() {
    this.router.navigate(['patient-appointment-view']);
  }

  makePreliminaryNutritionPlan(){
    var userDto : UserDto = {
          username: '',
          password: '',
          name: '',
          lastName: '',
          gender: 2,
          physicalActivity: 4,
          weight: 0,
          height: 0,
          age: 0,
          goal: 3,
          diseases: false,
          email: '',
          phoneNumber: '',
          address: ''
        };

    this.userProfileService.getUserInfo(this.loginService.getUserId()!).subscribe(
      (data) => {
        userDto = data;
        if(userDto.username == '' || userDto.password == '' || userDto.name == '' || userDto.lastName == '' || userDto.gender == 2
          || userDto.physicalActivity == 4 || userDto.weight == 0 || userDto.height == 0 || userDto.age ==0 || userDto.goal == 3
          || userDto.email == '' || userDto.phoneNumber == '' || userDto.address == ''){
          alert("Incomplete user data. Go to user profile and fill in all fields");
          return;
        } else {
          this.mealPlanService.generateMealPlan(this.loginService.getUserId()!).subscribe(
          (data) => {
            alert("Meal plan created")
          },
          (error) =>
            alert("Could not create meal plan")
          );
        }
      },
      (error) =>
        alert("An error occured")
    );
  }

  createAppointment() {
    this.router.navigate(['create-appointment']);
  }

  bookAppointment() {
    this.router.navigate(['book-appointment-view']);
  }

  nutritionistComplaintsView() {
    this.router.navigate(['nutritionist-complaints-view']);
  }

  blockNutritionistsView(){
    this.router.navigate(['block-nutritionist-view']);
  }

  complaintsAndAnswers(){
    this.router.navigate(['admin-complaint-and-answer-view']);
  }

  unblockUserView(){
    this.router.navigate(['unblock-user-view']);
  }
    
  createMealPlan(){
    this.router.navigate(['create-meal-plan-view']);
  }
}
