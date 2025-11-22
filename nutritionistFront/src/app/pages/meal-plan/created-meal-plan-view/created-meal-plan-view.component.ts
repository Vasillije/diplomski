import { Component, TemplateRef, ViewChild } from '@angular/core';
import { MealPlanService } from '../service/meal-plan.service';
import { LoginService } from '../../login/service/login.service';
import { CreatedMealPlanDto } from '../interfaces/CreatedMealPlanDto';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-created-meal-plan-view',
  standalone: false,
  
  templateUrl: './created-meal-plan-view.component.html',
  styleUrl: './created-meal-plan-view.component.css'
})
export class CreatedMealPlanViewComponent {
  dataSource : any;
  displayedColumns = ["patientsFullName", "date", "content"];
  allAppointments : CreatedMealPlanDto[] = [];

  @ViewChild('mealPlanDialog') mealPlanDialog!: TemplateRef<any>;

  constructor(private mealPlanService : MealPlanService, private loginService : LoginService, private dialog: MatDialog){}

  ngOnInit(){
    this.getCreatedNutritionistsMealPlans();
  }

  getCreatedNutritionistsMealPlans(){
    this.mealPlanService.getCreatedNutritionistsMealPlans(this.loginService.getUserId()!).subscribe(
      (data) => {
        this.dataSource = data;
        this.allAppointments = [...data];
        console.log(this.dataSource);
        this.sortByDate();
      },
    (error) =>
      alert("There was an error")
    );
  }

  openMealPlanDialog(mealPlan: string) {
    this.dialog.open(this.mealPlanDialog, {
      data: mealPlan,
      width: '600px'
    });
  }

  sortByDate() {
    const listToSort: CreatedMealPlanDto[] = [...this.dataSource];

    listToSort.sort((a, b) => {
      const dateA = new Date(a.dateCreated).getTime();
      const dateB = new Date(b.dateCreated).getTime();
      return dateB - dateA; // veći datum (noviji) ide gore
    });

    this.dataSource = [...listToSort];
  }

    
    
  filterDate(result : any){
    if(result){
      let listToFilter: CreatedMealPlanDto[] = [...this.allAppointments];
      let newList :  CreatedMealPlanDto[] =[];
      if(result.dateFrom && result.dateTo){
        const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
    
        for(let i of listToFilter){
          const itemDate: Date = new Date(i.dateCreated);
          if(itemDate >= result.dateFrom && itemDate <= dateTo){
              newList.push(i);
            }
        }
      } else if(result.dateFrom){
        for(let i of listToFilter){
            const itemDate: Date = new Date(i.dateCreated);
          if(itemDate >= result.dateFrom){
            newList.push(i);
          }
          }
        }
        else if(result.dateTo){
          const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
          for(let i of listToFilter){
            const itemDate = new Date(i.dateCreated);
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
        item.patientFullName.toLowerCase().includes(result.username.toLowerCase())
      );
  
      this.dataSource = [...filteredList];
      this.sortByDate();
    }
  }

  mealPlanContent(arg0: any,arg1: any) {
    throw new Error('Method not implemented.');
  }
  
  reset(){
    this.dataSource = [...this.allAppointments];
    this.sortByDate();
  }
}
