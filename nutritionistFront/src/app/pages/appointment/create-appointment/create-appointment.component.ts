import { Component } from '@angular/core';
import { CreateAppointmentDto } from '../interfaces/CreateAppointmentDto';
import { LoginService } from '../../login/service/login.service';
import { AppointmentService } from '../services/appointment.service';
import { Router } from '@angular/router';
import { AppointmentsByDateDto } from '../interfaces/AppointmentsByDateDto';

@Component({
  selector: 'app-create-appointment',
  standalone: false,
  
  templateUrl: './create-appointment.component.html',
  styleUrl: './create-appointment.component.css'
})
export class CreateAppointmentComponent {
  types = [
    {value: 20, viewValue: 'Consultation'},
    {value: 30, viewValue: 'Control'},
    {value: 60, viewValue: 'Examination'},
  ];
  minDate: Date = new Date();
  dataSource : AppointmentsByDateDto[] = [];
  displayedColumns: string[] = ['date', 'type', 'duration', 'patient'];
  appointmentType : string[] = [];
  patientFullName : string[] = [];
  date: Date | undefined;

  myFilter = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    return day !== 0 && day !== 6;
  };

  constructor(private loginService : LoginService, private appointmentService : AppointmentService, private router : Router){}
  
  
  createAppointment(result: any) {
    if(result.appointmentDate == null || result.time == null || result.type == null){
      alert("Fill in all information to create appointment");
      console.log(result.appointmentDate);
      
    }

    const dateTime = new Date(result.appointmentDate); 
    const time = result.time;
    dateTime.setHours(time.getHours());
    dateTime.setHours(dateTime.getHours() + 2);
    dateTime.setMinutes(time.getMinutes());

    const dto : CreateAppointmentDto = {
      dateAndTime: dateTime,
      duration: result.type,
      nutritionistId: this.loginService.getUserId()!
    };

    this.appointmentService.isAppointmentOverlapping(dto).subscribe(
      (isOverlapping) => {
        if (isOverlapping) {
          alert("Appointment is overlapping. Pick a different time.");
          return;
        }
  
        this.appointmentService.createAppointment(dto).subscribe(
          (data) => {
            alert("Appointment created successfully");
            this.router.navigate(['']);
          },
          (error) => 
            alert(error.message)
        );
      },
      (error) => {
        alert(error.message);
      }
    );
   }

   isAppointmentOverlapping(dto : CreateAppointmentDto){
    this.appointmentService.isAppointmentOverlapping(dto).subscribe(
      (data) =>{
        if(data == true){
          alert("Appointment is overlapping. Pick diferent time.");
          return ;
        }
        return true;
      },
      (error) =>{
          alert(error.message);
          return;
        }
        
    );
   }

  getAppointmentsByDate(date : string){
  this.appointmentService.getAppointmentsByDate(date).subscribe(
    (data) =>{
      this.dataSource = data;
      for(let i =0; i< this.dataSource.length; i++){
        if(this.dataSource[i].patientName && this.dataSource[i].patientLastName){
          this.patientFullName[i] = this.dataSource[i].patientName + " " + this.dataSource[i].patientLastName;
        }

         if(this.dataSource[i].duration == 20){
          this.appointmentType[i] = 'CONSULTATION';
        }
        else if(this.dataSource[i].duration == 30){
          this.appointmentType[i] = 'CONTROL';
        }
        else if(this.dataSource[i].duration == 60){
          this.appointmentType[i] = 'EXAMINATION';
        }
      }
    },
    (error) =>{
      alert("There was an error");
    }
    );
  }

  onDateChange(event: any) {
    const date = new Date(event.value);
    date.setDate(date.getDate() + 1);
    const selectedDate = date.toISOString().split('T')[0];
    this.getAppointmentsByDate(selectedDate);
  }


}
