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
  displayedColumns = ["patientsFullName", "duration", "date", "mealPlan"];

  constructor(private mealPlanService : MealPlanService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getPastBookedAppointmentsByNutritionistId();
  }

  getPastBookedAppointmentsByNutritionistId(){
    this.mealPlanService.getPastBookedAppointmentsByNutritionistId(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = [...data];
        this.allAppointments = data;
        console.log("data source : ",this.dataSource);
      },
    (error) =>
      alert("There was an error")
    );
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

}
