import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateComplaintDto } from '../interfaces/CreateComplaintDto';
import { NutritionistComplaintsDto } from '../interfaces/NutritionistComplaintsDto';
import { AnswerComplaintDto } from '../interfaces/AnswerComplaintDto';

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

  answerComplaint(dto : AnswerComplaintDto){
    return this.http.post<void>('/api/complaint/answerComplaint', dto);
  }
}
