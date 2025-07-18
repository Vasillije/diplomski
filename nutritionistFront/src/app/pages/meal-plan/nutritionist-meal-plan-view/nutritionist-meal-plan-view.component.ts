import { Component } from '@angular/core';

@Component({
  selector: 'app-nutritionist-meal-plan-view',
  standalone: false,
  
  templateUrl: './nutritionist-meal-plan-view.component.html',
  styleUrl: './nutritionist-meal-plan-view.component.css'
})
export class NutritionistMealPlanViewComponent {
  
  constructor(){}
  
  createMealPlan(result: any) {
    if(result.breakfast1 == '' || result.morningSnack1 == '' || result.lunch1 == '' || result.eveningSnack1 == '' || result.dinner1 == '' ||
       result.breakfast2 == '' || result.morningSnack2 == '' || result.lunch2 == '' || result.eveningSnack2 == '' || result.dinner2 == '' || 
       result.breakfast3 == '' || result.morningSnack3 == '' || result.lunch3 == '' || result.eveningSnack3 == '' || result.dinner3 == '' ||
       result.breakfast4 == '' || result.morningSnack4 == '' || result.lunch4 == '' || result.eveningSnack4 == '' || result.dinner4 == '' ||
       result.breakfast5 == '' || result.morningSnack5 == '' || result.lunch5 == '' || result.eveningSnack5 == '' || result.dinner5 == '' ||
       result.breakfast6 == '' || result.morningSnack6 == '' || result.lunch6 == '' || result.eveningSnack6 == '' || result.dinner6 == '' ||
       result.breakfast7 == '' || result.morningSnack7 == '' || result.lunch7 == '' || result.eveningSnack7 == '' || result.dinner7 == ''){
        alert("Fill in all fields. If some meal is skipped please type in 'NONE'.");
       }
  }

}
