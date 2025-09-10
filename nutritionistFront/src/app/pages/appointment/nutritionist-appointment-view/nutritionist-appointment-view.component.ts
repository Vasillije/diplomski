import { Component } from '@angular/core';
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
  displayedColumns: string[] = ['date', 'duration', 'patient', 'cancel'];
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
        
        this.allAppointments = this.allAppointments.map(appointment => {
        if (appointment.date) {
          const originalDate = new Date(appointment.date);
          originalDate.setHours(originalDate.getHours()); // Ovo će korigovati vreme
          return {
            ...appointment,
            formattedDate: this.formatDate(originalDate),
            formattedDuration: this.formatDuration(appointment.duration)
          };
        }
        return appointment;
        });

        this.dataSource = this.getFutureAppointments();
        this.sortByDate();
      },
      (error) =>
        alert(error.message)
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

  // DODAJ: Metoda za formatiranje trajanja
  formatDuration(duration: number): string {
    return duration + " min";
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
    return newList;
  }


getPastAppointments() {
  const newList = this.allAppointments
    .filter(a => new Date(a.date) <= this.currentDate)
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()); // opadajuće

  this.dataSource = newList;
}

  filterByName(result : any){
    if (result.username && result.username.trim() !== ''){
      let filteredList: NutritionistAppointmentDto[] = [];
      for (let i = 0; i < this.allAppointments.length; i++){
        const fullName = (this.allAppointments[i].patientFullName.toLowerCase());
    
        if (fullName.includes(result.username)) {
          filteredList.push(this.allAppointments[i]);
        }
      }

      const futureAppointments = filteredList
        .filter(a => new Date(a.date) > this.currentDate)
        .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()); // rastuće

      const pastAppointments = filteredList
        .filter(a => new Date(a.date) <= this.currentDate)
        .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()); // opadajuće
        
      this.dataSource = [...futureAppointments, ...pastAppointments];
    }
  }

}
