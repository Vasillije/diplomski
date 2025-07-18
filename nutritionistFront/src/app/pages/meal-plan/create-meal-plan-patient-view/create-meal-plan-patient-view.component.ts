import { Component } from '@angular/core';
import { MealPlanService } from '../service/meal-plan.service';
import { LoginService } from '../../login/service/login.service';
import { NutritionistMealPlanDto } from '../interfaces/NutritionistMealPlanDto';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { UserHealthInfoDto } from '../interfaces/UserHealthInfoDto';

@Component({
  selector: 'app-create-meal-plan-patient-view',
  standalone: false,

  templateUrl: './create-meal-plan-patient-view.component.html',
  styleUrl: './create-meal-plan-patient-view.component.css',
})
export class CreateMealPlanPatientViewComponent {
  constructor(
    private mealPlanService: MealPlanService,
    private loginService: LoginService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  appointmentId!: number;

  bmi: number = 0;
  physicalActivityText: string = '';
  goalText: string = '';
  genderText: string = '';

  userInfo: any;

  ngOnInit(): void {
    this.appointmentId = +this.route.snapshot.paramMap.get('id')!;
    console.log(this.appointmentId);
    this.getUsersInfo();
  }

  getUsersInfo() {
    this.mealPlanService
      .getUserHealthInfoByAppointmentId(this.appointmentId)
      .subscribe(
        (data) => {
          this.userInfo = data;
          console.log(this.userInfo);
          this.fillInfo();
        },
        (error) => alert('error')
      );
  }

  fillInfo() {
    if (this.userInfo.gender == 0) {
      this.bmi = 81.4 + 13.4 * this.userInfo.weight + 5.86 * this.userInfo.age;
    } else if (this.userInfo.gender == 1) {
      this.bmi =
        447.6 +
        9.25 * this.userInfo.weight +
        3.1 * this.userInfo.height +
        4.33 * this.userInfo.age;
    }

    if (this.userInfo.physicalActivity == 0) {
      this.physicalActivityText = 'NONE';
    } else if (this.userInfo.physicalActivity == 1) {
      this.physicalActivityText = 'LOW';
    } else if (this.userInfo.physicalActivity == 2) {
      this.physicalActivityText = 'MEDIUM';
    } else if (this.userInfo.physicalActivity == 3) {
      this.physicalActivityText = 'HIGH';
    } else {
      this.physicalActivityText = 'UNKNOWN';
    }

    if (this.userInfo.goal == 0) {
      this.goalText = 'LOOSE WEIGHT';
    } else if (this.userInfo.goal == 1) {
      this.goalText = 'HEALTH';
    } else if (this.userInfo.goal == 2) {
      this.goalText = 'GAIN WEIGHT';
    } else {
      this.goalText = 'UNKNOWN';
    }

    if (this.userInfo.gender == 0) {
      this.genderText = 'MALE';
    } else if (this.userInfo.gender == 1) {
      this.genderText = 'FEMALE';
    } else {
      this.genderText = 'UNKNOWN';
    }
  }

  createMealPlan(content: string) {
    var dto: NutritionistMealPlanDto = {
      nutritionistId: this.loginService.getUserId()!,
      patientId: this.userInfo.patientId,
      content: content,
      appointmentId: this.appointmentId,
    };

    if (content == '') {
      alert('Cant create empty meal plan');
      return;
    }

    console.log('ovo je dto ', dto);

    this.mealPlanService.createMealPlan(dto).subscribe(
      (data) => {
        alert('Meal plan created');
        this.router.navigate(['']);
      },
      (error) => alert('Could not create meal plan')
    );
  }
}
