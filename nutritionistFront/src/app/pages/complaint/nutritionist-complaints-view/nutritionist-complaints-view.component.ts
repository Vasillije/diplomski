import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { LoginService } from '../../login/service/login.service';
import { Router } from '@angular/router';
import { NutritionistComplaintsDto } from '../interfaces/NutritionistComplaintsDto';

@Component({
  selector: 'app-nutritionist-complaints-view',
  standalone: false,
  
  templateUrl: './nutritionist-complaints-view.component.html',
  styleUrl: './nutritionist-complaints-view.component.css'
})
export class NutritionistComplaintsViewComponent {
  dataSource : any;
  allAppointments: NutritionistComplaintsDto[] = [];

  displayedColumns: string[] = ['appointmentDate', 'patientsFullName', 'content', 'answer'];

  constructor(private complaintService : ComplaintService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getComplaintsByNutritionistId();
  }

  getComplaintsByNutritionistId(){
    this.complaintService.getComplaintsByNutritionistId(this.loginService.getUserId()!).subscribe(
    (data) => {
      this.dataSource = [...data];
      this.allAppointments = data;
      console.log(this.dataSource);
      this.sortByDate();
   },
   (error) => 
     alert(error.message));
  }

  answerComplaint(id: number) {
    this.router.navigate([`/complaint-answer-view/${id}`]);
  }

  sortByDate() {
    let temp;
    let listToSort: NutritionistComplaintsDto[] = [...this.dataSource];
  
    for (let i = 0; i < listToSort.length; i++) {
      for (let j = 0; j < listToSort.length; j++) {
        if (new Date(listToSort[i].appointmentDate) <= new Date(listToSort[j].appointmentDate)) {
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
      let listToFilter: NutritionistComplaintsDto[] = [...this.allAppointments];
      let newList :  NutritionistComplaintsDto[] =[];
      if(result.dateFrom && result.dateTo){
        const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
    
        for(let i of listToFilter){
          const itemDate: Date = new Date(i.appointmentDate);
          if(itemDate >= result.dateFrom && itemDate <= dateTo){
             newList.push(i);
           }
        }
      } else if(result.dateFrom){
       for(let i of listToFilter){
           const itemDate: Date = new Date(i.appointmentDate);
          if(itemDate >= result.dateFrom){
            newList.push(i);
          }
         }
       }
       else if(result.dateTo){
         const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
         for(let i of listToFilter){
           const itemDate = new Date(i.appointmentDate);
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
