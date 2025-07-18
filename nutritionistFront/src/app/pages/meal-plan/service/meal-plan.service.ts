import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserHealthInfoDto } from '../interfaces/UserHealthInfoDto';
import { PastBookedAppointmentsByNutritionistIdDto } from '../interfaces/PastBookedAppointmentsByNutritionistIdDto';
import { NutritionistMealPlanDto } from '../interfaces/NutritionistMealPlanDto';

@Injectable({
  providedIn: 'root'
})
export class MealPlanService {

  constructor(private http : HttpClient) { }

  examination : string = '';

  generateMealPlan(userId : number){
    return this.http.post<void>('/api/mealPlan/generateMealPlan', userId);
  }

  getPastBookedAppointmentsByNutritionistId(id : number){
    return this.http.get<PastBookedAppointmentsByNutritionistIdDto[]>('/api/appointment/getPastBookedAppointmentsByNutritionistId/'+id);
  }

  createMealPlan(dto : NutritionistMealPlanDto){
    return this.http.post<void>('/api/mealPlan/createMealPlan', dto);
  }

  getUserHealthInfoByAppointmentId(appointmentId : number){
    return this.http.get<UserHealthInfoDto>('/api/users/getUserHealthInfoByAppointmentId/'+appointmentId);
  }
}
