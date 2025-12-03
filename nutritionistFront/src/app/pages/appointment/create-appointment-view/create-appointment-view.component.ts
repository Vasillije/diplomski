import { Component } from '@angular/core';
import { AppointmentsByDateDto } from '../interfaces/AppointmentsByDateDto';
import { LoginService } from '../../login/service/login.service';
import { AppointmentService } from '../services/appointment.service';
import { Router } from '@angular/router';
import { CreateAppointmentDto } from '../interfaces/CreateAppointmentDto';
import { DateDto } from '../interfaces/DateDto';

@Component({
  selector: 'app-create-appointment-view',
  standalone: false,
  
  templateUrl: './create-appointment-view.component.html',
  styleUrl: './create-appointment-view.component.css'
})
export class CreateAppointmentViewComponent {
  selectedTime: string = '';
  availableTimes: string[] = [];
  types = [
    {value: 20, viewValue: 'Consultation'},
    {value: 40, viewValue: 'Control'},
    {value: 60, viewValue: 'Examination'},
  ];
  minDate: Date = new Date(new Date().setDate(new Date().getDate() + 1));
  dataSource : AppointmentsByDateDto[] = [];
  displayedColumns: string[] = ['date', 'duration', 'patient', "cancel"];
  //displayedColumns: string[] = ['date', 'duration', 'patient'];
  appointmentType : string[] = [];
  patientFullName : string[] = [];
  date: Date | undefined;

  myFilter = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    return day !== 0 && day !== 6;
  };

  constructor(private loginService : LoginService, private appointmentService : AppointmentService, private router : Router){}

  ngOnInit(){
    this.generateTimes();
  }
  
  generateTimes() {
    const times: string[] = [];
    for (let h = 8; h < 16; h++) {   // radno vreme od 08:00 do 18:00
      for (let m of [0, 20, 40]) {   // interval od 20 minuta
        const hh = h.toString().padStart(2, '0');
        const mm = m.toString().padStart(2, '0');
        times.push(`${hh}:${mm}`);
      }
    }
    this.availableTimes = times;
  }
  
  createAppointment(result: any) {
    if(!result.appointmentDate || !result.time ){
      alert("Fill in all information to create appointment");
      return;
    }

    const dateTime = new Date(result.appointmentDate); 
    const [hours, minutes] = result.time.split(':').map((x: string) => parseInt(x, 10));
    dateTime.setHours(hours, minutes, 0, 0);

    const dto : CreateAppointmentDto = {
      dateAndTime: dateTime,
      duration: 20,
      nutritionistId: this.loginService.getUserId()!
    };

    console.log(dto);

    this.appointmentService.isAppointmentOverlapping(dto).subscribe(
      (isOverlapping) => {
        if (isOverlapping) {
          alert("Appointment is overlapping. Pick a different time.");
          return;
        }
  
        this.appointmentService.createAppointment(dto).subscribe(
          (data) => {
          const appointmentDate = new Date(data.dateAndTime);
          const dateString = appointmentDate.toISOString().split('T')[0];
          
          // Ponovo učitaj termine za taj datum
          this.getAppointmentsByDate(dateString, this.loginService.getUserId()!);
            alert("Appointment created successfully");
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

  getAppointmentsByDate(date: string, id : number) {
  this.appointmentService.getAppointmentsByDate(date, id).subscribe(
    (data) => {
      this.patientFullName = [];
      this.appointmentType = [];
      this.dataSource = data;
      
      this.dataSource = data.sort((a, b) => {
        const dateA = new Date(a.date).getTime();
        const dateB = new Date(b.date).getTime();
        return dateA - dateB; // Sortiraj od najranijeg ka najkasnijem
      });

      for(let i = 0; i < this.dataSource.length; i++) {
        if(this.dataSource[i].patientName && this.dataSource[i].patientLastName) {
          this.patientFullName[i] = this.dataSource[i].patientName + " " + this.dataSource[i].patientLastName;
        }

        if(this.dataSource[i].date) {
          const originalDate = new Date(this.dataSource[i].date);
          originalDate.setHours(originalDate.getHours());
          this.dataSource[i].date = originalDate;
          
          // Formatiraj datum i duration u željenom formatu
          this.dataSource[i].formattedDate = this.formatDate(originalDate);
          this.dataSource[i].formattedDuration = this.formatDuration(this.dataSource[i].duration);
        }
      }
    },
    (error) => {
      alert("There was an error");
    }
  );
}

formatDate(date: Date): string {
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  
  return `${day}.${month}.${year} - ${hours}:${minutes}`;
}

formatDuration(duration: number): string {
  const formattedDuration = duration + " min"
  return formattedDuration;
}

  onDateChange(event: any) {
    const date = new Date(event.value);
    date.setDate(date.getDate() + 1);
    const selectedDate = date.toISOString().split('T')[0];
    this.getAppointmentsByDate(selectedDate, this.loginService.getUserId()!);
  }

  discard(result : any){
    if(result.time || result.appointmentDate)
    {
      window.location.reload();
      alert("Changes discarded successfully!");
    }
  }

  createDailyAppointments(result: any) {
    console.log(result.appointmentDate);
    if(!result.appointmentDate ){
      alert("You must select a date to create daily appointments");
      return;
    }

    this.appointmentService.createDailyAppointments(result.appointmentDate, this.loginService.getUserId()!).subscribe(
      (data) => {
        alert("Daily appointments created successfully");
        const d = result.appointmentDate;
        const date = `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`;
        this.getAppointmentsByDate(date, this.loginService.getUserId()!);
      },
      (error) => 
        alert("There was an error creating daily appointments")
    );
  }

  cancelAppointment(element: any) {
    this.appointmentService.cancelAppointmentNutritionist(element.appointmentId).subscribe(
       (data) =>{
         alert("Appointment canceled");
         console.log(typeof(element.date));
         var date = element.date.toISOString();
         this.getAppointmentsByDate(date, this.loginService.getUserId()!);
       },
       (error) =>
         alert("Could not cancel appointment")
     );
  }
}
