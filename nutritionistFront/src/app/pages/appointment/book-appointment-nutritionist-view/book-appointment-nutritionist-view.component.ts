import { Component } from '@angular/core';
import { BookDto } from '../interfaces/BookDto';
import { PatientAppointmentDto } from '../interfaces/PatientAppointmentDto';
import { AppointmentService } from '../services/appointment.service';
import { Router } from '@angular/router';
import { LoginService } from '../../login/service/login.service';
import { UserProfileService } from '../../user-profile/services/user-profile.service';
import { NutritionistAppointmentDto } from '../interfaces/NutritionistAppointmentDto';
import { PatientFullNameDto } from '../interfaces/PatientFullNameDto';

@Component({
  selector: 'app-book-appointment-nutritionist-view',
  standalone: false,
  
  templateUrl: './book-appointment-nutritionist-view.component.html',
  styleUrl: './book-appointment-nutritionist-view.component.css'
})
export class BookAppointmentNutritionistViewComponent {
  selectedType: string = '';
    selectedDate?: Date;
    availableTypes: string[] = ["CONSULTATION", "CONTROL", "EXAMINATION"];
    dataSource : NutritionistAppointmentDto[] = [];
    displayedColumns: string[] = ['date', 'duration', 'book'];
    minDate: Date = new Date(new Date().setDate(new Date().getDate() + 1));
    nutritionistAppointments : NutritionistAppointmentDto[] = [];
    allAppointments: NutritionistAppointmentDto[] = [];
    nutritionistNames : string[] = [];
    selectedPatientName : string = '';
    patientsFullNames : PatientFullNameDto[] = [];
  
    myFilter = (d: Date | null): boolean => {
      const day = (d || new Date()).getDay();
      return day !== 0 && day !== 6;
    };
  
    constructor(private appointmentService : AppointmentService, private router : Router, private loginService : LoginService,
      private userProfileService : UserProfileService){}
  
    ngOnInit(){
      this.getAvailableAppointmentsByNutritionistId();
      this.getPatientNames();
    }
  
    /////
    getAvailableAppointmentsByNutritionistId(){
      this.appointmentService.getAvailableAppointmentsByNutritionistId(this.loginService.getUserId()!).subscribe(
        (data) =>{
          this.allAppointments = data;
          this.dataSource = [...data];
          this.dataSource = this.sortByDate(this.dataSource);
          //this.getNutritionistNames();
        },
        (error) =>
          alert(error.message)
      );
    }
    
    getConsultations() {
      if (this.selectedDate && this.selectedType === "CONSULTATION" && this.selectedPatientName) {
        this.dataSource = this.filterByDate(this.selectedDate);
      }
    }
  
    getControls() {
      if (this.selectedDate && this.selectedType === "CONTROL" && this.selectedPatientName) {
        let controls: NutritionistAppointmentDto[] = [];
        let filteredList = this.filterByDate(this.selectedDate);
  
        for (let i = 0; i < filteredList.length - 1; i++) {
          const originalDate = new Date(filteredList[i].date);
          const datePlus20Min = new Date(originalDate.getTime() + 20 * 60 * 1000);
          const nextDate = new Date(filteredList[i + 1].date);
  
          if (datePlus20Min.getTime() === nextDate.getTime()) {
            controls.push({
              ...filteredList[i],
              duration: 40
            });
          }
        }
  
        this.dataSource = controls;
        return controls;
      }
      return [];
    }
  
    getExaminations() {
      if (this.selectedDate && this.selectedType === "EXAMINATION" && this.selectedPatientName) {
        let examinations: NutritionistAppointmentDto[] = [];
        let filteredList = this.filterByDate(this.selectedDate);
        
        for (let i = 0; i < filteredList.length - 2; i++) {
          const originalDate = new Date(filteredList[i].date);
          const datePlus20Min = new Date(originalDate.getTime() + 20 * 60 * 1000);
          const nextDate = new Date(filteredList[i + 1].date);
          const nextDatePlus20Min = new Date(nextDate.getTime() + 20 * 60 * 1000);
          const nextNextDate = new Date(filteredList[i + 2].date);
          
          if (datePlus20Min.getTime() === nextDate.getTime() && nextDatePlus20Min.getTime() == nextNextDate.getTime()) {
            examinations.push({
              ...filteredList[i],
              duration: 60
            });
          }
        }
        
        this.dataSource = examinations;
        return examinations;
      }
      return [];
    }
  
  
  
    filterByDate(result: Date) : NutritionistAppointmentDto[]{
      let sortedAndFilteredList : NutritionistAppointmentDto[] = [];
      if (result) {
        const selectedDate = new Date(result);
        selectedDate.setHours(0, 0, 0, 0);
  
        let newList: NutritionistAppointmentDto[] = [];
  
        for (let i of this.allAppointments) {
          const itemDate = new Date(i.date);
          itemDate.setHours(0, 0, 0, 0);
  
          if (itemDate.getTime() === selectedDate.getTime()) {
            newList.push(i);
          }
        }
  
        sortedAndFilteredList = this.sortByDate(newList);
      }
      return sortedAndFilteredList;
    }
  
    sortByDate(listToSort : NutritionistAppointmentDto[]) : NutritionistAppointmentDto[] {
      let temp;
  
      for (let i = 0; i < listToSort.length; i++) {
        for (let j = 0; j < listToSort.length; j++) {
          if (new Date(listToSort[i].date) <= new Date(listToSort[j].date)) {
            temp = listToSort[i];
            listToSort[i] = listToSort[j];
            listToSort[j] = temp;
          }
        }
      }
  
      return listToSort;
    }
  
    filterByName(name: string) {
      this.nutritionistAppointments = [];
      
      for(let i = 0; i < this.allAppointments.length; i++) {
        if(this.allAppointments[i].patientFullName === name) {
          this.nutritionistAppointments.push(this.allAppointments[i]);
        }
      }
      console.log('Filtrirani termini za:', name, this.nutritionistAppointments);
    }

    // book(element: any) {

    // }
  
    bookNutritionist(element: any) {
      let patientId = 0;
      for(let i of this.patientsFullNames) {
          if(i.patientFullName === this.selectedPatientName) {
              patientId = i.patientId;
              break;
          }
        }

      const bookDto: BookDto = {
        appointmentId: element.appointmentId,
        patientId: patientId,
        type: this.selectedType
      }
  
      this.appointmentService.book(bookDto).subscribe(
        (data) => {
          alert("Appointment booked successfully");
          
          this.appointmentService.getAvailableAppointmentsByNutritionistId(this.loginService.getUserId()!).subscribe(
            (newData) => {
              this.allAppointments = newData;
              if (this.selectedType === "CONTROL") {
                this.getControls();
              } 
              else if (this.selectedType === "CONSULTATION") {
                this.getConsultations();
              }
              else if (this.selectedType === "EXAMINATION") {
                this.getExaminations();
              }
            },
            (error) => {
              alert("Error refreshing appointments");
              console.error(error);
            }
          );
        },
        (error) => {
          alert("Error booking appointment");
          console.error(error);
        }
      );
    }
  
    onTypeChange(value: string) {
      this.selectedType = value;
      
      // Ažuriraj samo ako su već izabrani datum i nutricionista
      if (this.selectedDate && this.selectedPatientName) {
        if (value === 'CONTROL') {
          this.getControls();
        }
        else if(value === "CONSULTATION"){
          this.getConsultations();
        }
        else if(value === "EXAMINATION"){
          this.getExaminations();
        }
      }
    }
  
    onDateChange(date: Date | null) {
      if (!date) return;
      this.selectedDate = date;
      
      // Ažuriraj samo ako su već izabrani tip i nutricionista
      if (this.selectedType && this.selectedPatientName) {
        if (this.selectedType === 'CONTROL') {
          this.getControls();
        }
        else if(this.selectedType === "CONSULTATION"){
          this.getConsultations();
        }
        else if(this.selectedType === "EXAMINATION"){
          this.getExaminations();
        }
      }
    }
  
    getPatientNames() {
      this.appointmentService.getPatientFullNames().subscribe(
        (data) => {
          this.patientsFullNames = data;
          console.log(data);
        },
        (error) => {
          alert("Error fetching patient names");
        }
      );
      
    }
  
    onNameChange(value: string) {
      this.selectedPatientName = value;
      this.filterByName(value);
      
      // Resetuj ako nisu svi filteri postavljeni
      if (!this.selectedDate || !this.selectedType) {
        this.dataSource = [];
        return;
      }
      
      // Ažurirajte prikaz automatski za trenutni tip
      if (this.selectedType === 'CONTROL') {
        this.getControls();
      }
      else if(this.selectedType === "CONSULTATION"){
        this.getConsultations();
      }
      else if(this.selectedType === "EXAMINATION"){
        this.getExaminations();
      }
    }
}
