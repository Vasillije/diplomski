import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NutritionistMealPlanViewComponent } from './nutritionist-meal-plan-view/nutritionist-meal-plan-view.component';
import { MaterialModule } from '../../material/material.module';
import { CreateMealPlanViewComponent } from './create-meal-plan-view/create-meal-plan-view.component';
import { CreateMealPlanPatientViewComponent } from './create-meal-plan-patient-view/create-meal-plan-patient-view.component';



@NgModule({
  declarations: [
    NutritionistMealPlanViewComponent,
    CreateMealPlanViewComponent,
    CreateMealPlanPatientViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ]
})
export class MealPlanModule { }
