import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateComplaintDto } from '../interfaces/CreateComplaintDto';
import { LoginService } from '../../login/service/login.service';

@Component({
  selector: 'app-complaint-view',
  standalone: false,
  
  templateUrl: './complaint-view.component.html',
  styleUrl: './complaint-view.component.css'
})
export class ComplaintViewComponent {

  constructor(private complaintService : ComplaintService, private route: ActivatedRoute, private router : Router, private loginService : LoginService){}

  currentDate : Date = new Date();

  id!: number;
  
  ngOnInit(): void {
    this.id = +this.route.snapshot.paramMap.get('id')!;
    console.log(this.id);
  }

  makeComplaint(result: any) {
    const dto : CreateComplaintDto = {
      appointmentId: this.id,
      content: result.complaint,
      patientId: this.loginService.getUserId()!,
      date: this.currentDate
    }

    console.log(dto);
    console.log(typeof(dto.date));

    if(dto.content.length > 250){
      alert("Content is too long. Max is 255 characters!");
      return;
    }

    this.complaintService.makeComplaint(dto).subscribe(
      (data) => {
        alert("Complaint has been sent.");
        this.router.navigate(['']);
      },
      (error) => 
        alert(error.message)
    );
  }

}
