import { Component } from '@angular/core';
import { MealPlanService } from '../service/meal-plan.service';
import { LoginService } from '../../login/service/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-meal-plan-view',
  standalone: false,
  
  templateUrl: './create-meal-plan-view.component.html',
  styleUrl: './create-meal-plan-view.component.css'
})
export class CreateMealPlanViewComponent {
  dataSource : any;

  displayedColumns = ["appointmentId", "patientId", "duration", "date", "mealPlan"];

  constructor(private mealPlanService : MealPlanService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getPastBookedAppointmentsByNutritionistId();
  }

  getPastBookedAppointmentsByNutritionistId(){
    this.mealPlanService.getPastBookedAppointmentsByNutritionistId(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data;
        console.log("data source : ",this.dataSource);
      },
    (error) =>
      alert("There was an error")
    );
  }

  createMealPlan(element: any) {
    this.router.navigate([`create-meal-plan-patient-view/${element.appointmentId}`]);
  }

}
