import { Component } from '@angular/core';
import {AfterViewInit, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import { LoginService } from '../../login/service/login.service';
import { AppointmentService } from '../services/appointment.service';
import { NutritionistAppointmentDto } from '../interfaces/NutritionistAppointmentDto';

@Component({
  selector: 'app-nutritionist-appointment-view',
  standalone: false,
  
  templateUrl: './nutritionist-appointment-view.component.html',
  styleUrl: './nutritionist-appointment-view.component.css',
})
export class NutritionistAppointmentViewComponent {
  dataSource : any;
  displayedColumns: string[] = ['date', 'type', 'duration', 'patient', 'cancel'];
  appointmentType : string[] = [];
  patientFullName : string[] = [];
  currentDate : Date = new Date();
  allAppointments: NutritionistAppointmentDto[] = [];

  searchText : string = '';

  constructor(private loginService : LoginService, private appointmentService : AppointmentService){}

  ngOnInit(){
    this.getNutritionistAppointments();
  }

  getNutritionistAppointments(){
    this.appointmentService.getNutritionistAppointments(this.loginService.getUserId()!).subscribe(
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

  getOnlyExaminations() {
    this.appointmentService.getNutritionistAppointments(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data.filter((dto: NutritionistAppointmentDto) => dto.duration === 60);
        this.mapData();
        this.sortByDate();
      },
      (error) => {
        alert(error.message);
      }
    );
  }
    
  getOnlyControls() {
    this.appointmentService.getNutritionistAppointments(this.loginService.getUserId()!).subscribe(
       (data) => {
        this.dataSource = data.filter((dto: NutritionistAppointmentDto) => dto.duration === 40);
        this.mapData();
        this.sortByDate();
       },
       (error) => {
        alert(error.message);
      }
    );
   }
    
    getOnlyConsultation() {
      this.appointmentService.getNutritionistAppointments(this.loginService.getUserId()!).subscribe(
        (data) => {
          this.dataSource = data.filter((dto: NutritionistAppointmentDto) => dto.duration === 20);
          this.mapData();
          this.sortByDate();
        },
        (error) => {
          alert(error.message);
        }
      );
    }

    private mapData(): void {
    this.patientFullName = [];
    this.appointmentType = [];

    for (let i = 0; i < this.dataSource.length; i++) {
      this.patientFullName[i] =
        this.dataSource[i].patientName + " " + this.dataSource[i].patientLastName;

      if (this.dataSource[i].duration == 20) {
        this.appointmentType[i] = "CONSULTATION";
      } else if (this.dataSource[i].duration == 40) {
        this.appointmentType[i] = "CONTROL";
      } else if (this.dataSource[i].duration == 60) {
        this.appointmentType[i] = "EXAMINATION";
      }
    }
  }

  canCancelAppointment(element : any){
    var date = element.date;
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

  cancelAppointment(element : any){
    this.appointmentService.cancelAppointmentNutritionist(element.appointmentId).subscribe(
       (data) =>{
         alert("Appointment canceled");
         this.getNutritionistAppointments();
       },
       (error) =>
         alert("Could not cancel appointment")
     );
  }

  sortByDate(){
      var temp;
      var listToSort : NutritionistAppointmentDto[] = [...this.dataSource];
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
      let listToFilter: NutritionistAppointmentDto[] = [...this.allAppointments];
      let newList :  NutritionistAppointmentDto[] =[];
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

  getFutureAppointments(){
    let listToFilter: NutritionistAppointmentDto[] = [...this.allAppointments];
    let newList :  NutritionistAppointmentDto[] =[];
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
    let listToFilter: NutritionistAppointmentDto[] = [...this.allAppointments];
    let newList :  NutritionistAppointmentDto[] =[];
    for(let i of listToFilter){
      const date = new Date(i.date);
      if(date <= this.currentDate){
        newList.push(i);
      }
    }
    this.dataSource = newList;
    this.sortByDate();
  }

  filterByName(result : any){
    if (result.username && result.username.trim() !== ''){
      let filteredList: NutritionistAppointmentDto[] = [];
      for (let i = 0; i < this.allAppointments.length; i++){
        const fullName = (this.allAppointments[i].patientName + ' ' + this.allAppointments[i].patientLastName).toLowerCase();
    
        if (fullName.includes(result.username)) {
          filteredList.push(this.allAppointments[i]);
        }
      }
      this.dataSource = [...filteredList];
      this.mapData();
      this.sortByDate();
    }
  }

  reset(){
    this.dataSource = [...this.allAppointments];
    this.mapData();
    this.sortByDate();
  }
}
