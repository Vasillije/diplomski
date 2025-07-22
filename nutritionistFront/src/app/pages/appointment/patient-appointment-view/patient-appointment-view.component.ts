import { Component } from '@angular/core';
import { AppointmentService } from '../services/appointment.service';
import { Router } from '@angular/router';
import { LoginService } from '../../login/service/login.service';
import { PatientAppointmentDto } from '../interfaces/PatientAppointmentDto';

@Component({
  selector: 'app-patient-appointment-view',
  standalone: false,
  
  templateUrl: './patient-appointment-view.component.html',
  styleUrl: './patient-appointment-view.component.css'
})
export class PatientAppointmentViewComponent {
  dataSource : any;
  displayedColumns: string[] = ['date', 'type', 'duration', 'nutritionist', 'cancel', 'complaint'];
  appointmentType : string[] = [];
  nutritionistFullName : string[] = [];
  allAppointments: PatientAppointmentDto[] = [];

  myFilter = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    return day !== 0 && day !== 6;
  };

  currentDate : Date = new Date();

  constructor(private appointmentService : AppointmentService, private router : Router, private loginService : LoginService){}

  ngOnInit(){
    this.getPatientAppointments();
  }

  getPatientAppointments(){
    this.appointmentService.getPatientAppointments(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.allAppointments = data;
        this.dataSource = [...data];
        this.mapData();
        this.sortByDate();
      },
      (error) =>
        alert(error.message)
    );
  }

  cancelAppointmentPatient(appointmentId : number) {
    this.appointmentService.cancelAppointmentPatient(appointmentId).subscribe(
      (data) => {
        alert("Appointment successfully canceled");
        this.getPatientAppointments();
      },
      (error) =>
        alert("error.message")
    );
  }

  canMakeComplaint(element: any): boolean {
    var date = element.date;
    if (typeof date === 'string') {
      date = new Date(date);
    }
    if(date.getTime() < this.currentDate.getTime()){
      return true;
    }
    else{
      return false;
    }
  }

  makeComplaint(result : any){
    console.log(result.appointmentId);
    this.router.navigate([`complaint-view/${result.appointmentId}`]);
  }

  canBeCancelled(date : any) : boolean{
    if (typeof date === 'string') {
      date = new Date(date);
    }
    if(date.getTime() > this.currentDate.getTime()){
      return true;
    }
    else{
      return false;
    }
  }

  getOnlyExaminations() {
    this.appointmentService.getPatientAppointments(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 60);
        this.mapData();
        this.sortByDate();
      },
      (error) => {
        alert(error.message);
      }
    );
  }
  
  getOnlyControls() {
    this.appointmentService.getPatientAppointments(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 40);
        this.mapData();
        this.sortByDate();
      },
      (error) => {
        alert(error.message);
      }
    );
  }
  
  getOnlyConsultation() {
    this.appointmentService.getPatientAppointments(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 20);
        this.mapData();
        this.sortByDate();
      },
      (error) => {
        alert(error.message);
      }
    );
  }
  
  sortByDate(){
    var temp;
    var listToSort : PatientAppointmentDto[] = [...this.dataSource];
    for(let i = 0; i< listToSort.length; i++){
      for(let j = 0; j< listToSort.length; j++){
        if(listToSort[i].date <= listToSort[j].date){
          temp = listToSort[i];
          listToSort[i] = listToSort[j];
          listToSort[j] = temp;
         }
      }
    }
    this.dataSource = [...listToSort];
  }

  filterDate(result : any){
        if(result){
          let listToFilter: PatientAppointmentDto[] = [...this.allAppointments];
          let newList :  PatientAppointmentDto[] =[];
          if(result.dateFrom && result.dateTo){
            const dateTo = new Date(result.dateTo);
            dateTo.setHours(23, 59, 59, 999);
  
            for(let i of listToFilter){
              const itemDate: Date = new Date(i.date);
              if(itemDate >= result.dateFrom && itemDate <= dateTo){
                newList.push(i);
              }
            }
          } else if(result.dateFrom){
            for(let i of listToFilter){
              const itemDate: Date = new Date(i.date);
              if(itemDate >= result.dateFrom){
                newList.push(i);
              }
            }
          }
          else if(result.dateTo){
            const dateTo = new Date(result.dateTo);
            dateTo.setHours(23, 59, 59, 999);
            for(let i of listToFilter){
              const itemDate = new Date(i.date);
              if(itemDate <= dateTo){
                newList.push(i);
              }
            }
          }
          this.dataSource = newList;
          this.sortByDate();
        }
      }

  filterByName(result : any){
    if (result.username && result.username.trim() !== ''){
      let filteredList: PatientAppointmentDto[] = [];
      for (let i = 0; i < this.allAppointments.length; i++){
        const fullName = (this.allAppointments[i].nutritionistName + ' ' + this.allAppointments[i].nutritionistLastName).toLowerCase();

        if (fullName.includes(result.username)) {
        filteredList.push(this.allAppointments[i]);
        }
      }
      this.dataSource = [...filteredList];
      this.mapData();
      this.sortByDate();
    }
  }

  private mapData(): void {
    this.nutritionistFullName = [];
    this.appointmentType = [];

    for (let i = 0; i < this.dataSource.length; i++) {
      this.nutritionistFullName[i] =
        this.dataSource[i].nutritionistName + " " + this.dataSource[i].nutritionistLastName;

      if (this.dataSource[i].duration == 20) {
        this.appointmentType[i] = "CONSULTATION";
      } else if (this.dataSource[i].duration == 40) {
        this.appointmentType[i] = "CONTROL";
      } else if (this.dataSource[i].duration == 60) {
        this.appointmentType[i] = "EXAMINATION";
      }
    }
  }

  reset(){
    this.dataSource = [...this.allAppointments];
    this.mapData();
    this.sortByDate();
  }

  getFutureAppointments(){
    let listToFilter: PatientAppointmentDto[] = [...this.allAppointments];
    let newList :  PatientAppointmentDto[] =[];
    for(let i of listToFilter){
      const date = new Date(i.date);
      if(date > this.currentDate){
        newList.push(i);
      }
    }
    this.dataSource = newList;
    this.sortByDate();
  }
  
  
  getPastAppointments(){
    let listToFilter: PatientAppointmentDto[] = [...this.allAppointments];
    let newList :  PatientAppointmentDto[] =[];
    for(let i of listToFilter){
      const date = new Date(i.date);
      if(date <= this.currentDate){
        newList.push(i);
      }
    }
    this.dataSource = newList;
    this.sortByDate();
  }

}

