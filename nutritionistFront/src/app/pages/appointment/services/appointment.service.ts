import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateAppointmentDto } from '../interfaces/CreateAppointmentDto';
import { PatientAppointmentDto } from '../interfaces/PatientAppointmentDto';
import { BookAppointmentDto } from '../interfaces/BookAppointmentDto';
import { NutritionistAppointmentDto } from '../interfaces/NutritionistAppointmentDto';
import { AppointmentsByDateDto } from '../interfaces/AppointmentsByDateDto';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  constructor(private http : HttpClient) { }

  createAppointment(dto : CreateAppointmentDto){
    return this.http.post<CreateAppointmentDto>("/api/appointment/createAppointment", dto);
  }

  getAvailableAppointments(){
    return this.http.get<PatientAppointmentDto[]>("/api/appointment/getAvailableAppointments");
  }

  bookAppointment(dto : BookAppointmentDto){
    return this.http.post<void>("/api/appointment/bookAppointment", dto);
  }

  getNutritionistAppointments(id : number){
    return this.http.get<NutritionistAppointmentDto[]>("/api/appointment/getAppointmentsByNutritionistId/"+id);
  }

  getPatientAppointments(id : number){
    return this.http.get<PatientAppointmentDto[]>("/api/appointment/getAppointmentsByPatientId/"+id);
  }

  cancelAppointmentPatient(id : number){
    return this.http.post<void>("api/appointment/cancelAppointmentPatient", id);
  }

  isAppointmentOverlapping(dto : CreateAppointmentDto){
    return this.http.post<Boolean>("api/appointment/isAppointmentOverlapping", dto);
  }

  cancelAppointmentNutritionist(id : number){
    return this.http.post<void>("api/appointment/cancelAppointmentNutritionist", id);
  }
  
  getAppointmentsByDate(date : string){
    return this.http.get<AppointmentsByDateDto[]>("api/appointment/getAppointmentsByDate/"+date)
  }
}
