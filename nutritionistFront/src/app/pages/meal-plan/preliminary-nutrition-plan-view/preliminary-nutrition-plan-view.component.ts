import { ChangeDetectorRef, Component } from '@angular/core';
import { UserProfileService } from '../../user-profile/services/user-profile.service';
import { LoginService } from '../../login/service/login.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientInfoDto } from '../../user-profile/interfaces/PatientInfoDto';
import { HealthInfoDto } from '../../user-profile/interfaces/HealthInfoDto';
import { MealPlanService } from '../service/meal-plan.service';
import { SharedService } from '../../../shared/service/shared.service';

@Component({
  selector: 'app-preliminary-nutrition-plan-view',
  standalone: false,
  
  templateUrl: './preliminary-nutrition-plan-view.component.html',
  styleUrl: './preliminary-nutrition-plan-view.component.css'
})
export class PreliminaryNutritionPlanViewComponent {
  constructor(private userProfileService : UserProfileService, private loginService : LoginService, private route : ActivatedRoute,
    private mealPlanService : MealPlanService, private cdr: ChangeDetectorRef, private sharedService : SharedService, private router : Router){}
  
  user! : PatientInfoDto;
  username : string = '';
  patientId! : number;
  hasPreliminaryPlan : boolean = false;

  genderOptions = [
    { value: 'MALE', viewValue: 'Male' },
    { value: 'FEMALE', viewValue: 'Female' },
    { value: 'EMPTY', viewValue: 'Empty'}
  ];

  activityOptions = [
    { value: 'NONE', viewValue: 'None' },
    { value: 'LOW', viewValue: 'Low' },
    { value: 'MEDIUM', viewValue: 'Medium' },
    { value: 'HIGH', viewValue: 'High' },
    { value: 'EMPTY', viewValue: 'Empty'}
  ];
  
  goalOptions = [
    { value: 'LOOSE_WEIGHT', viewValue: 'Loose weight' },
    { value: 'HEALTH', viewValue: 'Health' },
    { value: 'GAIN_WEIGHT', viewValue: 'Gain weight'},
    { value: 'EMPTY', viewValue: 'Empty'}
  ];

ngOnInit(){
  //this.patientId = +this.route.snapshot.paramMap.get('id')!;
  this.patientId = this.loginService.getUserId()!;
  this.loadUserInfo();
}

ngOnDestroy(): void {
    // Kada se komponenta uništi, postavi na false
    this.sharedService.setHasPreliminaryPlan(false);
  }


loadUserInfo(){
  this.userProfileService.getPatientsInfo(this.patientId).subscribe(
    (data) => {
      this.user = data;
      this.patientId = this.user.patientId;
      console.log(data);
    },
    (error) =>
      alert(error.message)
  );
}

getPreliminaryNutritionPlan(){
  const userConfirmation = confirm("NOTE: You can get preliminary nutrition plan only once. Are you sure you want to proceed?");
    
    if (!userConfirmation) {
      return;
    }
    
    this.userProfileService.getUserInfo(this.loginService.getUserId()!).subscribe(
      (data) => {
        const userDto = data;
        if(userDto.username == '' || userDto.password == '' || userDto.name == '' || 
          userDto.lastName == '' || userDto.gender == 2 || userDto.physicalActivity == 4 || 
          userDto.weight == 0 || userDto.height == 0 || userDto.age == 0 || userDto.goal == 3 ||
          userDto.email == '' || userDto.phoneNumber == '' || userDto.address == ''){
          alert("Incomplete user data. Go to user profile and fill in all fields");
          return;
        } else {
          this.mealPlanService.generateMealPlan(this.loginService.getUserId()!).subscribe(
            (data) => {
              alert("Meal plan created");
              this.sharedService.setHasPreliminaryPlan(true);
              // Postavi flag i osveži prikaz
              this.cdr.detectChanges(); // Osveži view da bi sakrio dugme
            },
            (error) => {
              alert("Could not create meal plan");
            }
          );
        }
      },
      (error) => {
        alert("An error occurred");
      }
    );
}

createPreliminaryMealPlan(result: any) {
  let gender: number = -1;
  let physicalActivity: number = -1;
  let goal: number= -1;
  let diseases: boolean = false;

  if(result.goal == "LOOSE_WEIGHT"){
    goal = 0;
  } 
  else if(result.goal == "HEALTH"){
    goal = 1;
  }
  else if(result.goal == "GAIN_WEIGHT"){
    goal = 2;
  }
  else if(result.goal == "EMPTY"){
    goal = 3;
  }

  if (result.gender == "MALE") {
    gender = 0;
  } 
  else if (result.gender == "FEMALE") {
    gender = 1;
  }
  else if (result.gender == "EMPTY") {
    gender = 2;
  }

  if (result.physicalActivity == "NONE") {
    physicalActivity = 0;
  } 
  else if (result.physicalActivity == "LOW") {
    physicalActivity = 1;
  }
  else if (result.physicalActivity == "MEDIUM") {
    physicalActivity = 2;
  } 
  else if (result.physicalActivity == "HIGH") {
    physicalActivity = 3;
  }
  else if (result.physicalActivity == "EMPTY") {
    physicalActivity = 4;
  }

  if (result.diseases == "YES") {
    diseases = true;
  } 
  else if (result.diseases == "NO") {
    diseases = false;
  }

  const dto : HealthInfoDto = {
    id : this.patientId,
    gender: gender,
    physicalActivity: physicalActivity,
    weight: result.weight,
    height: result.height,
    age: result.age,
    goal: goal,
    diseases: diseases
  };

  if(dto.weight < 30){
    alert("Weight must be bigger than 30")
    return;
  }

  if(dto.height < 100){
    alert("Height must be bigger than 100 cm")
    return;
  }

  if(dto.age < 13){
    alert("Age must be bigger than 13")
    return;
  }

  this.userProfileService.changeHealthInfo(dto).subscribe(
    (data) =>{
      console.log("Promenjeni podaci:", data);
      this.loadUserInfo();
      this.getPreliminaryNutritionPlan();
      this.router.navigate(['/']);
    },
  (error) =>
    alert(error.message)
  );
  }
}
