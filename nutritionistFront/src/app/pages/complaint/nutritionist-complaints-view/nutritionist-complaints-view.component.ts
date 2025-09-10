import { Component } from '@angular/core';
import { ComplaintService } from '../service/complaint.service';
import { LoginService } from '../../login/service/login.service';
import { Router } from '@angular/router';
import { NutritionistComplaintsDto } from '../interfaces/NutritionistComplaintsDto';
import { AnsweredNutritionistComplaintsDto } from '../interfaces/AnsweredNutritionistComplaintsDto';

@Component({
  selector: 'app-nutritionist-complaints-view',
  standalone: false,
  
  templateUrl: './nutritionist-complaints-view.component.html',
  styleUrl: './nutritionist-complaints-view.component.css'
})
export class NutritionistComplaintsViewComponent {
  dataSource : any;
  allAppointments: NutritionistComplaintsDto[] = [];
  answeredComplaints: AnsweredNutritionistComplaintsDto[] = [];
  showUnansweredComplaints: boolean = true;
  showAnsweredComplaints: boolean = false;
  
  displayedColumns: string[] = ['appointmentDate', 'complaintDate', 'patientsFullName', 'content', 'answer'];

  displayedColumnsAnswered: string[] = ['appointmentDate', 'complaintDate', 'answerDate', 'patientsFullName', 'content', 'answerContent'];

  constructor(private complaintService : ComplaintService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
    this.getComplaintsByNutritionistId();
  }

  getComplaintsByNutritionistId(){
  this.complaintService.getComplaintsByNutritionistId(this.loginService.getUserId()!).subscribe(
    (data) => {
      this.dataSource = [...data];

      this.dataSource = this.dataSource.map((complaint: NutritionistComplaintsDto) => {
        // Kreiraj novi objekat sa formatiranim datumima
        const formattedComplaint = {...complaint};
        
        if (complaint.appointmentDate) {
          const originalAppointmentDate = new Date(complaint.appointmentDate);
          originalAppointmentDate.setHours(originalAppointmentDate.getHours());
          formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
        }

        if (complaint.complaintDate) {
          const originalComplaintDate = new Date(complaint.complaintDate);
          originalComplaintDate.setHours(originalComplaintDate.getHours());
          formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
        }

        return formattedComplaint;
      });

      this.allAppointments = [...this.dataSource];
      this.sortByDate();
      console.log(data);
    },
    (error) => alert(error.message)
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

  getAnsweredComplaintsByNutritionistId(){
  this.showUnansweredComplaints = false;
  this.showAnsweredComplaints = true;
  this.complaintService.getAnsweredComplaintsByNutritionistId(this.loginService.getUserId()!).subscribe(
    (data) => {
      this.dataSource = [...data];
      this.answeredComplaints = data;
      
      // Formatiraj datume za odgovorene žalbe
      this.dataSource = this.dataSource.map((complaint: AnsweredNutritionistComplaintsDto) => {
        const formattedComplaint = {...complaint};
        
        if (complaint.appointmentDate) {
          const originalAppointmentDate = new Date(complaint.appointmentDate);
          originalAppointmentDate.setHours(originalAppointmentDate.getHours());
          formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
        }

        if (complaint.complaintDate) {
          const originalComplaintDate = new Date(complaint.complaintDate);
          originalComplaintDate.setHours(originalComplaintDate.getHours());
          formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
        }

        if (complaint.answerDate) {
          const originalAnswerDate = new Date(complaint.answerDate);
          originalAnswerDate.setHours(originalAnswerDate.getHours());
          formattedComplaint.formattedAnswerDate = this.formatDate(originalAnswerDate);
        }

        return formattedComplaint;
      });
      
      this.answeredComplaints = data;
      this.sortAnsweredByDate();
    },
    (error) => alert(error.message)
  );
}

  answerComplaint(id: number) {
    this.router.navigate([`/complaint-answer-view/${id}`]);
  }

  sortAnsweredByDate() {
    this.dataSource.sort((a: AnsweredNutritionistComplaintsDto, b: AnsweredNutritionistComplaintsDto) => 
    new Date(a.appointmentDate).getTime() - new Date(b.appointmentDate).getTime()
    );
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
  
  
  filterDate(result: any) {
  if (this.showUnansweredComplaints) {
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
  } else if (this.showAnsweredComplaints) {
    if (result) {
      let listToFilter: AnsweredNutritionistComplaintsDto[] = [...this.answeredComplaints];
      let newList: AnsweredNutritionistComplaintsDto[] = [];
      
      if (result.dateFrom && result.dateTo) {
        const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
    
        for (let i of listToFilter) {
          const itemDate: Date = new Date(i.appointmentDate);
          if (itemDate >= result.dateFrom && itemDate <= dateTo) {
            newList.push(i);
          }
        }
      } else if (result.dateFrom) {
        for (let i of listToFilter) {
          const itemDate: Date = new Date(i.appointmentDate);
          if (itemDate >= result.dateFrom) {
            newList.push(i);
          }
        }
      } else if (result.dateTo) {
        const dateTo = new Date(result.dateTo);
        dateTo.setHours(23, 59, 59, 999);
        for (let i of listToFilter) {
          const itemDate = new Date(i.appointmentDate);
          if (itemDate <= dateTo) {
            newList.push(i);
          }
        }
      }
      
      // Formatiraj datume nakon filtriranja
      this.dataSource = newList.map((complaint: AnsweredNutritionistComplaintsDto) => {
        const formattedComplaint = {...complaint};
        
        if (complaint.appointmentDate) {
          const originalAppointmentDate = new Date(complaint.appointmentDate);
          originalAppointmentDate.setHours(originalAppointmentDate.getHours());
          formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
        }

        if (complaint.complaintDate) {
          const originalComplaintDate = new Date(complaint.complaintDate);
          originalComplaintDate.setHours(originalComplaintDate.getHours());
          formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
        }

        if (complaint.answerDate) {
          const originalAnswerDate = new Date(complaint.answerDate);
          originalAnswerDate.setHours(originalAnswerDate.getHours());
          formattedComplaint.formattedAnswerDate = this.formatDate(originalAnswerDate);
        }

        return formattedComplaint;
      });
      
      this.sortAnsweredByDate();
    }
  }
}
  
  filterByName(result: any) {
    if (result.username && result.username.trim() !== '') {
      if (this.showUnansweredComplaints) {
        const filteredList = this.allAppointments.filter(item =>
          item.patientsFullName.toLowerCase().includes(result.username.toLowerCase())
        );
        this.dataSource = [...filteredList];
        this.sortByDate();
      } else if (this.showAnsweredComplaints) {
        const filteredList = this.answeredComplaints.filter(item =>
          item.patientsFullName.toLowerCase().includes(result.username.toLowerCase())
        );
        
        // Formatiraj datume nakon filtriranja
        this.dataSource = filteredList.map((complaint: AnsweredNutritionistComplaintsDto) => {
          const formattedComplaint = {...complaint};
          
          if (complaint.appointmentDate) {
            const originalAppointmentDate = new Date(complaint.appointmentDate);
            originalAppointmentDate.setHours(originalAppointmentDate.getHours());
            formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
          }

          if (complaint.complaintDate) {
            const originalComplaintDate = new Date(complaint.complaintDate);
            originalComplaintDate.setHours(originalComplaintDate.getHours());
            formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
          }

          if (complaint.answerDate) {
            const originalAnswerDate = new Date(complaint.answerDate);
            originalAnswerDate.setHours(originalAnswerDate.getHours());
            formattedComplaint.formattedAnswerDate = this.formatDate(originalAnswerDate);
          }

          return formattedComplaint;
        });
        
        this.sortAnsweredByDate();
      }
    }
  }

  
  reset(){
    this.showUnansweredComplaints = true;
    this.showAnsweredComplaints = false;
    this.dataSource = [...this.allAppointments];
    this.sortByDate();
  }

  filterDateComplaint(result: any) {
    if (this.showUnansweredComplaints) {
      if (result) {
        let listToFilter: NutritionistComplaintsDto[] = [...this.allAppointments];
        let newList: NutritionistComplaintsDto[] = [];
        
        if (result.dateFrom && result.dateTo) {
          const dateTo = new Date(result.dateTo);
          dateTo.setHours(23, 59, 59, 999);

          for (let i of listToFilter) {
            const itemDate: Date = new Date(i.complaintDate);
            if (itemDate >= result.dateFrom && itemDate <= dateTo) {
              newList.push(i);
            }
          }
        } else if (result.dateFrom) {
          for (let i of listToFilter) {
            const itemDate: Date = new Date(i.complaintDate);
            if (itemDate >= result.dateFrom) {
              newList.push(i);
            }
          }
        } else if (result.dateTo) {
          const dateTo = new Date(result.dateTo);
          dateTo.setHours(23, 59, 59, 999);
          for (let i of listToFilter) {
            const itemDate = new Date(i.complaintDate);
            if (itemDate <= dateTo) {
              newList.push(i);
            }
          }
        }
        
        // Formatiraj datume nakon filtriranja
        this.dataSource = newList.map((complaint: NutritionistComplaintsDto) => {
          const formattedComplaint = {...complaint};
          
          if (complaint.appointmentDate) {
            const originalAppointmentDate = new Date(complaint.appointmentDate);
            originalAppointmentDate.setHours(originalAppointmentDate.getHours());
            formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
          }

          if (complaint.complaintDate) {
            const originalComplaintDate = new Date(complaint.complaintDate);
            originalComplaintDate.setHours(originalComplaintDate.getHours());
            formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
          }

          return formattedComplaint;
        });
        
        this.sortByDate();
      }
    } else if (this.showAnsweredComplaints) {
      // Logika za odgovorene žalbe po complaintDate
      if (result) {
        let listToFilter: AnsweredNutritionistComplaintsDto[] = [...this.answeredComplaints];
        let newList: AnsweredNutritionistComplaintsDto[] = [];
        
        if (result.dateFrom && result.dateTo) {
          const dateTo = new Date(result.dateTo);
          dateTo.setHours(23, 59, 59, 999);

          for (let i of listToFilter) {
            const itemDate: Date = new Date(i.complaintDate);
            if (itemDate >= result.dateFrom && itemDate <= dateTo) {
              newList.push(i);
            }
          }
        } else if (result.dateFrom) {
          for (let i of listToFilter) {
            const itemDate: Date = new Date(i.complaintDate);
            if (itemDate >= result.dateFrom) {
              newList.push(i);
            }
          }
        } else if (result.dateTo) {
          const dateTo = new Date(result.dateTo);
          dateTo.setHours(23, 59, 59, 999);
          for (let i of listToFilter) {
            const itemDate = new Date(i.complaintDate);
            if (itemDate <= dateTo) {
              newList.push(i);
            }
          }
        }
        
        // Formatiraj datume nakon filtriranja
        this.dataSource = newList.map((complaint: AnsweredNutritionistComplaintsDto) => {
          const formattedComplaint = {...complaint};
          
          if (complaint.appointmentDate) {
            const originalAppointmentDate = new Date(complaint.appointmentDate);
            originalAppointmentDate.setHours(originalAppointmentDate.getHours());
            formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
          }

          if (complaint.complaintDate) {
            const originalComplaintDate = new Date(complaint.complaintDate);
            originalComplaintDate.setHours(originalComplaintDate.getHours());
            formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
          }

          if (complaint.answerDate) {
            const originalAnswerDate = new Date(complaint.answerDate);
            originalAnswerDate.setHours(originalAnswerDate.getHours());
            formattedComplaint.formattedAnswerDate = this.formatDate(originalAnswerDate);
          }

          return formattedComplaint;
        });
        
        this.sortAnsweredByDate();
      }
    }
  }

  filterDateAnswer(result: any) {
    if (this.showAnsweredComplaints) {
      if (result) {
        let listToFilter: AnsweredNutritionistComplaintsDto[] = [...this.answeredComplaints];
        let newList: AnsweredNutritionistComplaintsDto[] = [];
        
        // Konvertuj result vrednosti u Date objekte ako već nisu
        const dateFrom = result.dateFrom ? new Date(result.dateFrom) : null;
        const dateTo = result.dateTo ? new Date(result.dateTo) : null;
        
        if (dateTo) {
          dateTo.setHours(23, 59, 59, 999);
        }

        for (let i of listToFilter) {
          const itemDate: Date = new Date(i.answerDate);
          
          let includeItem = true;
          
          if (dateFrom && itemDate < dateFrom) {
            includeItem = false;
          }
          
          if (dateTo && itemDate > dateTo) {
            includeItem = false;
          }
          
          if (includeItem) {
            newList.push(i);
          }
        }
        
        // Formatiraj datume nakon filtriranja
        this.dataSource = newList.map((complaint: AnsweredNutritionistComplaintsDto) => {
          const formattedComplaint = {...complaint};
          
          if (complaint.appointmentDate) {
            const originalAppointmentDate = new Date(complaint.appointmentDate);
            originalAppointmentDate.setHours(originalAppointmentDate.getHours());
            formattedComplaint.formattedAppointmentDate = this.formatDate(originalAppointmentDate);
          }

          if (complaint.complaintDate) {
            const originalComplaintDate = new Date(complaint.complaintDate);
            originalComplaintDate.setHours(originalComplaintDate.getHours());
            formattedComplaint.formattedComplaintDate = this.formatDate(originalComplaintDate);
          }

          if (complaint.answerDate) {
            const originalAnswerDate = new Date(complaint.answerDate);
            originalAnswerDate.setHours(originalAnswerDate.getHours());
            formattedComplaint.formattedAnswerDate = this.formatDate(originalAnswerDate);
          }

          return formattedComplaint;
        });
        
        this.sortAnsweredByDate();
      }
    }
  }
}
