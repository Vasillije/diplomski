import { Component } from '@angular/core';
import { MealPlanService } from '../service/meal-plan.service';
import { LoginService } from '../../login/service/login.service';
import { Router } from '@angular/router';
import { PastBookedAppointmentsByNutritionistIdDto } from '../interfaces/PastBookedAppointmentsByNutritionistIdDto';

@Component({
  selector: 'app-create-meal-plan-view',
  standalone: false,
  
  templateUrl: './create-meal-plan-view.component.html',
  styleUrl: './create-meal-plan-view.component.css'
})
export class CreateMealPlanViewComponent {
  dataSource : any;
  allAppointments : PastBookedAppointmentsByNutritionistIdDto[] = [];
  displayedColumns = ["patientsFullName", "date", "mealPlan", "healthInfo"];

  constructor(private mealPlanService : MealPlanService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getPastBookedExaminationsByNutritionistId();
  }

  getPastBookedExaminationsByNutritionistId(){
    this.mealPlanService.getPastBookedExaminationsByNutritionistId(this.loginService.getUserId()!).subscribe(
      (data) => {
        const filteredData = this.filterFirstAppointmentsOnly(data);
        this.dataSource = [...filteredData];
        this.allAppointments = filteredData;
        console.log("data source : ", this.dataSource);
      },
    (error) =>
      alert("There was an error")
    );
  }

  filterFirstAppointmentsOnly(appointments: PastBookedAppointmentsByNutritionistIdDto[]): PastBookedAppointmentsByNutritionistIdDto[] {
    // Grupiši appointmente po dateBooked vremenu
    const appointmentsByDateBooked = new Map<string, PastBookedAppointmentsByNutritionistIdDto[]>();
    
    for (const appointment of appointments) {
      // Koristimo timestamp za tačno poređenje
      const dateBookedKey = new Date(appointment.dateBooked).getTime().toString();
      
      if (!appointmentsByDateBooked.has(dateBookedKey)) {
        appointmentsByDateBooked.set(dateBookedKey, []);
      }
      appointmentsByDateBooked.get(dateBookedKey)!.push(appointment);
    }
    
    // Iz svake grupe uzmi samo prvi appointment (najraniji po datumu)
    const result: PastBookedAppointmentsByNutritionistIdDto[] = [];
    
    for (const [dateBookedKey, appointmentGroup] of appointmentsByDateBooked) {
      if (appointmentGroup.length > 0) {
        // Sortiraj grupu po datumu appointmenta i uzmi prvi
        const sortedGroup = appointmentGroup.sort((a, b) => 
          new Date(a.date).getTime() - new Date(b.date).getTime()
        );
        result.push(sortedGroup[0]);
      }
    }
    
    return result;
  }

  createMealPlan(element: any) {
    this.router.navigate([`create-meal-plan-patient-view/${element.appointmentId}`]);
  }

  sortByDate() {
      let temp;
      let listToSort: PastBookedAppointmentsByNutritionistIdDto[] = [...this.dataSource];
    
      for (let i = 0; i < listToSort.length; i++) {
        for (let j = 0; j < listToSort.length; j++) {
          if (new Date(listToSort[i].date) <= new Date(listToSort[j].date)) {
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
        let listToFilter: PastBookedAppointmentsByNutritionistIdDto[] = [...this.allAppointments];
        let newList :  PastBookedAppointmentsByNutritionistIdDto[] =[];
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
    
  filterByName(result: any) {
    if (result.username && result.username.trim() !== '') {
      const filteredList = this.allAppointments.filter(item =>
        item.patientsFullName.toLowerCase().includes(result.username.toLowerCase())
      );
  
      this.dataSource = [...filteredList];
      this.sortByDate();
    }
  }
  
    
  reset(){
    this.dataSource = [...this.allAppointments];
    this.sortByDate();
  }

  usersHealthInfo(id: number) {
    console.log(id);
    this.router.navigate(["user-health-info", id]);
  }

}
