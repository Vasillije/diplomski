import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { LoginService } from '../../login/service/login.service';
import { AnsweredNutritionistComplaintsDto } from '../interfaces/AnsweredNutritionistComplaintsDto';

@Component({
  selector: 'app-answered-nutritionist-complaints-view',
  standalone: false,
  
  templateUrl: './answered-nutritionist-complaints-view.component.html',
  styleUrl: './answered-nutritionist-complaints-view.component.css'
})
export class AnsweredNutritionistComplaintsViewComponent {
  constructor(private complaintService : ComplaintService, private loginService : LoginService){}

  dataSource : any;
  

  
}
