import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateComplaintDto } from '../interfaces/CreateComplaintDto';
import { NutritionistComplaintsDto } from '../interfaces/NutritionistComplaintsDto';
import { AnswerComplaintDto } from '../interfaces/AnswerComplaintDto';
import { AnsweredNutritionistComplaintsDto } from '../interfaces/AnsweredNutritionistComplaintsDto';
import { ComplaintInfoDto } from '../interfaces/ComplaintInfoDto';
import { AppointmentInfoDto } from '../interfaces/AppointmentInfoDto';

@Injectable({
  providedIn: 'root'
})
export class ComplaintService {

  constructor(private http : HttpClient) { }

  makeComplaint(dto : CreateComplaintDto){
    return this.http.post<void>('/api/complaint/createComplaint', dto);
  }

  getComplaintsByNutritionistId(id : number){
    return this.http.get<NutritionistComplaintsDto[]>('/api/complaint/getComplaintsByNutritionistId/'+id);
  }

  getAnsweredComplaintsByNutritionistId(id : number){
    return this.http.get<AnsweredNutritionistComplaintsDto[]>('/api/complaint/getAnsweredComplaintsByNutritionistId/'+id);
  }

  answerComplaint(dto : AnswerComplaintDto){
    return this.http.post<void>('/api/complaint/answerComplaint', dto);
  }

  getComplaintInfo(id : number){
    return this.http.get<ComplaintInfoDto>('/api/complaint/getComplaintInfo/'+id);
  }

  getAppointmentInfo(id : number){
    return this.http.get<AppointmentInfoDto>('/api/appointment/getAppointmentInfo/'+id);
  }
}
