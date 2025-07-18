import { Component } from '@angular/core';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-admin-complaint-and-answer-view',
  standalone: false,
  
  templateUrl: './admin-complaint-and-answer-view.component.html',
  styleUrl: './admin-complaint-and-answer-view.component.css'
})
export class AdminComplaintAndAnswerViewComponent {
  dataSource : any;
  displayedColumns = ['complaintId', 'complaintContent', 'answerId', 'answerContent', 'nutritionistId', 'patientId', 'appointmentId'];

  constructor(private adminService : AdminService){}

  ngOnInit(){
    this.getComplaintsAndAnswers();
  }

  getComplaintsAndAnswers(){
    this.adminService.getComplaintsAndAnswers().subscribe(
      (data) => {
        this.dataSource = data;
        console.log(this.dataSource);
      },
      (error) =>
        alert("Could not get complaints and answers")
    );
  }

}
