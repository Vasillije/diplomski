import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NutritionistMealPlanViewComponent } from './nutritionist-meal-plan-view/nutritionist-meal-plan-view.component';
import { MaterialModule } from '../../material/material.module';
import { CreateMealPlanViewComponent } from './create-meal-plan-view/create-meal-plan-view.component';
import { CreateMealPlanPatientViewComponent } from './create-meal-plan-patient-view/create-meal-plan-patient-view.component';
import { UserProfileModule } from '../user-profile/user-profile.module';
import { PreliminaryNutritionPlanViewComponent } from './preliminary-nutrition-plan-view/preliminary-nutrition-plan-view.component';



@NgModule({
  declarations: [
    NutritionistMealPlanViewComponent,
    CreateMealPlanViewComponent,
    CreateMealPlanPatientViewComponent,
    PreliminaryNutritionPlanViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    UserProfileModule
  ]
})
export class MealPlanModule { }
