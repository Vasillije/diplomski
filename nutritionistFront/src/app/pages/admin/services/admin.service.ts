import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NutritionistsForBlockDto } from '../interfaces/NutritionistsForBlockDto';
import { ComplaintAndAnswerDto } from '../interfaces/ComplaintAndAnswerDto';
import { UnblockUsersDto } from '../interfaces/UnblockUsersDto';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http : HttpClient) { }

  getNutritionistsForBlock(){
    return this.http.get<NutritionistsForBlockDto[]>('/api/users/getNutritionistsForBlock');
  }

  blockNutritionist(id : number){
    return this.http.post<void>("api/users/blockNutritionist", id);
  }

  getComplaintsAndAnswers(){
    return this.http.get<ComplaintAndAnswerDto[]>('api/complaint/getComplaintsAndAnswers');
  }

  getBlockedUsers(){
    return this.http.get<UnblockUsersDto[]>('api/users/getBlockedUsers');
  }

  unblockUser(id : number){
    return this.http.post<void>('api/users//unblockUser', id);
  }

  getAllUsers(){
    return this.http.get<UnblockUsersDto[]>('api/users/getAllUsers');
  }
}
