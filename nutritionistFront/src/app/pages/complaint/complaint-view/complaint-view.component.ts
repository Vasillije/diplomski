import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CreateComplaintDto } from '../interfaces/CreateComplaintDto';
import { LoginService } from '../../login/service/login.service';
import { AppointmentInfoDto } from '../interfaces/AppointmentInfoDto';

@Component({
  selector: 'app-complaint-view',
  standalone: false,
  
  templateUrl: './complaint-view.component.html',
  styleUrl: './complaint-view.component.css'
})
export class ComplaintViewComponent {

  constructor(private complaintService : ComplaintService, private route: ActivatedRoute, private router : Router, private loginService : LoginService){}
  appointmentInfo? : AppointmentInfoDto;
  currentDate : Date = new Date();
  appointmentType : string = '';

  id!: number;
  
  ngOnInit(): void {
    this.id = +this.route.snapshot.paramMap.get('id')!;
    console.log(this.id);
    this.getAppointmentInfo();
  }

  getAppointmentInfo() {
    this.complaintService.getAppointmentInfo(this.id).subscribe(
      (data) => {
        // backend šalje Date, mi ga pretvaramo u lokalno vreme
        const date = new Date(data.appointmentDate);
        data.appointmentDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
        this.appointmentType = data.type;
        this.appointmentInfo = data;
        console.log("Fixed date:", this.appointmentInfo.appointmentDate);
      },
      (error) => alert("Could not get appointment info")
    );
  }


  makeComplaint(result: any) {
    const dto : CreateComplaintDto = {
      appointmentId: this.id,
      content: result.complaint,
      patientId: this.loginService.getUserId()!,
      date: this.currentDate,
      appointmentType: this.appointmentType
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

  cancel(){
    alert("Changes cancelled");
    this.router.navigate(["patient-appointment-view"]);
  }

}
