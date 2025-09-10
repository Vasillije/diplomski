import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PastBookedAppointmentsByNutritionistIdDto } from '../interfaces/PastBookedAppointmentsByNutritionistIdDto';
import { NutritionistMealPlanDto } from '../interfaces/NutritionistMealPlanDto';
import { UserInfoDto } from '../interfaces/UserInfoDto';

@Injectable({
  providedIn: 'root'
})
export class MealPlanService {

  constructor(private http : HttpClient) { }

  examination : string = '';

  generateMealPlan(userId : number){
    return this.http.post<void>('/api/mealPlan/generateMealPlan', userId);
  }

  getPastBookedExaminationsByNutritionistId(id : number){
    return this.http.get<PastBookedAppointmentsByNutritionistIdDto[]>('/api/appointment/getPastBookedExaminationsByNutritionistId/'+id);
  }

  createMealPlan(dto : NutritionistMealPlanDto){
    return this.http.post<void>('/api/mealPlan/createMealPlan', dto);
  }

  getUserInfoByAppointmentId(appointmentId : number){
    return this.http.get<UserInfoDto>('/api/users/getUserInfoByAppointmentId/'+appointmentId);
  }
}
