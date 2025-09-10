import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../../login/service/login.service';
import { AnswerComplaintDto } from '../interfaces/AnswerComplaintDto';
import { ComplaintInfoDto } from '../interfaces/ComplaintInfoDto';

@Component({
  selector: 'app-complaint-answer-view',
  standalone: false,
  
  templateUrl: './complaint-answer-view.component.html',
  styleUrl: './complaint-answer-view.component.css'
})
export class ComplaintAnswerViewComponent {

  constructor(private complaintService : ComplaintService, private route: ActivatedRoute, private router : Router, private loginService : LoginService){}
  
  id!: number;
  currentDate : Date = new Date();
  complaintDto? : ComplaintInfoDto;
  
  ngOnInit(): void {
    this.id = +this.route.snapshot.paramMap.get('id')!;
    this.getComplaintInfo(this.id);
  }

  getComplaintInfo(id:number){
    this.complaintService.getComplaintInfo(id).subscribe(
      (data) =>{
        this.complaintDto = data;
        console.log(data);
      },
      (error) =>
        alert("error gettinng complaint info")
    );
  }
  
  answerComplaint(result: any) {
    console.log(result.asnwer);
    const dto :  AnswerComplaintDto ={
      complaintId: this.id,
      nutritionistId: this.loginService.getUserId()!,
      content: result.answer,
      date: this.currentDate
    };

    if(result.answer == ""){
      alert("Answer can not be empty")
      return;
    }

    if(dto.content.length > 255){
      alert("Content is too long. Max is 255 characters!");
      return;
    }

    this.complaintService.answerComplaint(dto).subscribe(
      (data) =>{
        alert("Answer sent successfully");
        this.router.navigate(['nutritionist-complaints-view']);
      },
      (error)=>
        alert("Errror answering to complaint")
    );
  }

  goBack() {
    this.router.navigate(["nutritionist-complaints-view"]);
  }

}
