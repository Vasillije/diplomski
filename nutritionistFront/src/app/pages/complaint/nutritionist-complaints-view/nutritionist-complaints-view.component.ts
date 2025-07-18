import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { LoginService } from '../../login/service/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nutritionist-complaints-view',
  standalone: false,
  
  templateUrl: './nutritionist-complaints-view.component.html',
  styleUrl: './nutritionist-complaints-view.component.css'
})
export class NutritionistComplaintsViewComponent {
  dataSource : any;

  displayedColumns: string[] = ['complaintId', 'appointmentId', 'content', 'answer'];

  constructor(private complaintService : ComplaintService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getComplaintsByNutritionistId();
  }

  getComplaintsByNutritionistId(){
    this.complaintService.getComplaintsByNutritionistId(this.loginService.getUserId()!).subscribe(
    (data) => {
      this.dataSource = data;
      console.log(this.dataSource);
   },
   (error) => 
     alert(error.message));
  }

  answerComplaint(id: number) {
    this.router.navigate([`/complaint-answer-view/${id}`]);
  }

}
