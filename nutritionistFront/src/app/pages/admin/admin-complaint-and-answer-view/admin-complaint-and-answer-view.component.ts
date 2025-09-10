import { Component } from '@angular/core';
import { AdminService } from '../services/admin.service';
import { ComplaintAndAnswerDto } from '../interfaces/ComplaintAndAnswerDto';

@Component({
  selector: 'app-admin-complaint-and-answer-view',
  standalone: false,
  
  templateUrl: './admin-complaint-and-answer-view.component.html',
  styleUrl: './admin-complaint-and-answer-view.component.css'
})
// export class AdminComplaintAndAnswerViewComponent {

//   dataSource : any;
//   displayedColumns = ['complaintDate', 'complaintContent', 'answerDate', 'answerContent', 'nutritionistFullName', 'patientFullName', 'appointmentDate'];

//   constructor(private adminService : AdminService){}

//   ngOnInit(){
//     this.getComplaintsAndAnswers();
//   }

//   getComplaintsAndAnswers(){
//     this.adminService.getComplaintsAndAnswers().subscribe(
//       (data) => {
//         this.dataSource = data;
//         console.log(this.dataSource);
//       },
//       (error) =>
//         alert("Could not get complaints and answers")
//     );
//   }

//   filterAnswerDate(result: any) {
//     throw new Error('Method not implemented.');
//   }

//   filterComplaintDate(result: any) {
//     throw new Error('Method not implemented.');
//   }

//   filterAppointmentDate(result: any) {
//     throw new Error('Method not implemented.');
//   }

//   filterByName(result : any){

//   }

//   reset(){

//   }

// }

export class AdminComplaintAndAnswerViewComponent {

  dataSource: ComplaintAndAnswerDto[] = [];
  allData: ComplaintAndAnswerDto[] = [];
  displayedColumns = ['complaintDate', 'complaintContent', 'answerDate', 'answerContent', 'nutritionistFullName', 'patientFullName', 'appointmentDate'];

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.getComplaintsAndAnswers();
  }

  getComplaintsAndAnswers() {
    this.adminService.getComplaintsAndAnswers().subscribe(
      (data: ComplaintAndAnswerDto[]) => {
        this.allData = data;
        this.dataSource = [...data];
        this.sortByDate();
      },
      (error) => alert("Could not get complaints and answers")
    );
  }

  // Filter by Appointment date
  filterAppointmentDate(result: any) {
    if (result) {
      const filtered = this.filterByDateRange(this.allData, 'appointmentDate', result.dateFromAppointment, result.dateToAppointment);
      this.dataSource = filtered;
      this.sortByDate();
    }
  }

  // Filter by Complaint date
  filterComplaintDate(result: any) {
    if (result) {
      const filtered = this.filterByDateRange(this.allData, 'complaintDate', result.dateFromComplaint, result.dateToComplaint);
      this.dataSource = filtered;
      this.sortByDate();
    }
  }

  // Filter by Answer date
  filterAnswerDate(result: any) {
    if (result) {
      const filtered = this.filterByDateRange(this.allData, 'answerDate', result.dateFromAnswer, result.dateToAnswer);
      this.dataSource = filtered;
      this.sortByDate();
    }
  }

  // Pomoćna funkcija za filtriranje po datumu na bilo kom polju DTO-a
  private filterByDateRange(list: ComplaintAndAnswerDto[], dateField: keyof ComplaintAndAnswerDto, dateFrom?: string, dateTo?: string): ComplaintAndAnswerDto[] {
    let fromDate: Date | null = dateFrom ? new Date(dateFrom) : null;
    let toDate: Date | null = dateTo ? new Date(dateTo) : null;

    if (toDate) {
      toDate.setHours(23, 59, 59, 999);
    }

    return list.filter(item => {
      let itemDateRaw = item[dateField];
      // appointmentDate je string, complaintDate i answerDate su Date
      let itemDate = (typeof itemDateRaw === 'string') ? new Date(itemDateRaw) : itemDateRaw;

      if (!itemDate) return false; // ako nema datuma ne prikazuj

      if (fromDate && toDate) {
        return itemDate >= fromDate && itemDate <= toDate;
      } else if (fromDate) {
        return itemDate >= fromDate;
      } else if (toDate) {
        return itemDate <= toDate;
      } else {
        return true;
      }
    });
  }

  // Sortiranje tabele po complaintDate (ili možeš promeniti da sortiraš po nekom drugom datumu ako želiš)
  sortByDate() {
    this.dataSource.sort((a, b) => {
      const dateA = a.complaintDate ? new Date(a.complaintDate).getTime() : 0;
      const dateB = b.complaintDate ? new Date(b.complaintDate).getTime() : 0;
      return dateA - dateB;
    });
  }

  // Filter po imenu (nutritionist ili patient)
  filterByName(result: any) {
    if (result && result.username && result.username.trim() !== '') {
      const search = result.username.toLowerCase();
      this.dataSource = this.allData.filter(item =>
        item.nutritionistFullName.toLowerCase().includes(search) ||
        item.patientFullName.toLowerCase().includes(search)
      );
      this.sortByDate();
    }
  }

  // Resetovanje filtera na početne podatke
  reset() {
    this.dataSource = [...this.allData];
    this.sortByDate();
  }
}

