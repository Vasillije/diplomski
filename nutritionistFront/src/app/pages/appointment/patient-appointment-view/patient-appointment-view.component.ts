import { Component } from '@angular/core';
import { AppointmentService } from '../services/appointment.service';
import { Router } from '@angular/router';
import { LoginService } from '../../login/service/login.service';
import { PatientAppointmentDto } from '../interfaces/PatientAppointmentDto';
import { CancelAppointmentDto } from '../interfaces/CancelAppointmentDto';

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
  consultations: PatientAppointmentDto[] = [];
  controls: PatientAppointmentDto[] = [];
  examinations: PatientAppointmentDto[] = [];
  allMergedAppointments: PatientAppointmentDto[] = [];

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
        console.log(data);
        this.getPatientMergedAppointments();
        this.getFutureAppointments();
        this.sortByDate();
      },
      (error) =>
        alert(error.message)
    );
  }

  getPatientMergedAppointments() {
    let mergedConsultations = this.getConsultations();
    let mergedControls = this.getControls();
    let mergedExaminations = this.getExaminations();

    this.allMergedAppointments = [
      ...mergedConsultations,
      ...mergedControls,
      ...mergedExaminations
    ];

    return this.allMergedAppointments;
  }

  getConsultations(){
    let consultations = [];
    for(let i = 0; i<this.allAppointments.length; i++){
      if(this.allAppointments[i].type == "CONSULTATION"){
        consultations.push(this.allAppointments[i]);
      }
    }
    this.dataSource = consultations;
    this.sortByDate();
    return consultations;
  }

  getControls() {
    let consultations = this.allAppointments
      .filter(a => a.type === "CONTROL")
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

    let tableConsultations: PatientAppointmentDto[] = [];

    for (let i = 0; i < consultations.length; i++) {
      const current = consultations[i];
      const currentDate = new Date(current.date);
      const currentDateBooked = new Date(current.dateBooked);
      const currentNutritionistId = current.nutritionistId;

      const hasNext = consultations.some(next => {
        if (next === current) return false;

        const nextDate = new Date(next.date);
        const nextDateBooked = new Date(next.dateBooked);

        return (
          nextDate.getTime() - currentDate.getTime() === 20 * 60000 &&
          currentNutritionistId === next.nutritionistId &&
          Math.abs(currentDateBooked.getTime() - nextDateBooked.getTime()) < 1000
        );
      });

      if (hasNext) {
        let updated = { ...current, duration: 40 }; // trajanje kontrole 40 minuta
        tableConsultations.push(updated);
      }
    }

    console.log("Kontrole:", tableConsultations);
    this.controls = tableConsultations;
    this.dataSource = this.controls;
    this.sortByDate();
    return this.controls;
  }

  getExaminations() {
    let examinations = this.allAppointments
      .filter(a => a.type === "EXAMINATION")
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

    let tableExaminations: PatientAppointmentDto[] = [];

    for (let i = 0; i < examinations.length; i++) {
      const current = examinations[i];
      const currentDate = new Date(current.date);
      const currentDateBooked = new Date(current.dateBooked);
      const currentNutritionistId = current.nutritionistId;

      const hasNext1 = examinations.find(next => {
        const nextDate = new Date(next.date);
        const nextDateBooked = new Date(next.dateBooked);
        return (
          nextDate.getTime() - currentDate.getTime() === 20 * 60000 &&
          currentNutritionistId === next.nutritionistId &&
          Math.abs(currentDateBooked.getTime() - nextDateBooked.getTime()) < 1000
        );
      });

      if (hasNext1) {
        const nextDate1 = new Date(hasNext1.date);

        const hasNext2 = examinations.find(next => {
          const nextDate = new Date(next.date);
          const nextDateBooked = new Date(next.dateBooked);
          return (
            nextDate.getTime() - nextDate1.getTime() === 20 * 60000 &&
            currentNutritionistId === next.nutritionistId &&
            Math.abs(currentDateBooked.getTime() - nextDateBooked.getTime()) < 1000
          );
        });

        if (hasNext2) {
          let updated = { ...current, duration: 60 }; // trajanje examination 60 minuta
          tableExaminations.push(updated);
        }
      }
    }

    console.log("Pregledi (examinations):", tableExaminations);
    this.examinations = tableExaminations;
    this.dataSource = this.examinations;
    this.sortByDate();
    return this.examinations;
  }



  cancelAppointmentPatient(element : any) {
    const dto : CancelAppointmentDto = {
      appointmentId: element.appointmentId,
      type: element.type
    }
    this.appointmentService.cancelAppointmentPatient(dto).subscribe(
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
  
  sortByDate() {
    // Podeli appointmente na buduće i prošle
    const futureAppointments = this.dataSource.filter((appointment: PatientAppointmentDto) => {
      const appointmentDate = new Date(appointment.date);
      return appointmentDate > this.currentDate;
    });

    const pastAppointments = this.dataSource.filter((appointment: PatientAppointmentDto) => {
      const appointmentDate = new Date(appointment.date);
      return appointmentDate <= this.currentDate;
    });

    // Sortiraj buduće appointmente: najbliži -> najdalji
    futureAppointments.sort((a: PatientAppointmentDto, b: PatientAppointmentDto) => {
      const dateA = new Date(a.date);
      const dateB = new Date(b.date);
      return dateA.getTime() - dateB.getTime(); // rastuće
    });

    // Sortiraj prošle appointmente: najbliži (najskoriji) -> najdalji (najstariji)
    pastAppointments.sort((a: PatientAppointmentDto, b: PatientAppointmentDto) => {
      const dateA = new Date(a.date);
      const dateB = new Date(b.date);
      return dateB.getTime() - dateA.getTime(); // opadajuće (najskoriji prvi)
    });

    // Spoji buduće i prošle appointmente
    this.dataSource = [...futureAppointments, ...pastAppointments];
  }

  filterDate(result : any){
    if(result){
      let listToFilter: PatientAppointmentDto[] = [...this.allMergedAppointments];
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
      } 
      else if(result.dateFrom){
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
      for (let i = 0; i < this.allMergedAppointments.length; i++){
        const fullName = (this.allAppointments[i].nutritionistFullName).toLowerCase();
        
        if (fullName.includes(result.username)) {
          filteredList.push(this.allMergedAppointments[i]);
        }
      }
    
      const futureAppointments = filteredList
        .filter(a => new Date(a.date) > this.currentDate)
        .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()); // rastuće
    
      const pastAppointments = filteredList
        .filter(a => new Date(a.date) <= this.currentDate)
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()); // opadajuće
    
      // spajanje budućih i prošlih
      this.dataSource = [...futureAppointments, ...pastAppointments];
    }
  }

  getFutureAppointments(){
    let listToFilter: PatientAppointmentDto[] = [...this.allMergedAppointments];
    let newList :  PatientAppointmentDto[] =[];
    for(let i of listToFilter){
      const date = new Date(i.date);
      if(date > this.currentDate){
        newList.push(i);
      }
    }
    this.dataSource = newList;
    this.sortByDate();
    return newList;
  }

getPastAppointments() {
  const newList = this.allMergedAppointments
    .filter(a => new Date(a.date) <= this.currentDate)
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()); // opadajuće

  this.dataSource = newList;
}

}

