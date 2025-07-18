import { Component } from '@angular/core';
import { AppointmentService } from '../services/appointment.service';
import { PatientAppointmentDto } from '../interfaces/PatientAppointmentDto';
import { Router } from '@angular/router';
import { BookAppointmentDto } from '../interfaces/BookAppointmentDto';
import { LoginService } from '../../login/service/login.service';
import { UserProfileService } from '../../user-profile/services/user-profile.service';
import { UserDto } from '../../user-profile/interfaces/UserDto';

@Component({
  selector: 'app-book-appointment-view',
  standalone: false,
  
  templateUrl: './book-appointment-view.component.html',
  styleUrl: './book-appointment-view.component.css'
})
export class BookAppointmentViewComponent {
  dataSource : PatientAppointmentDto[] = [];
  appointmentType : string[] = [];
  nutritionistFullName : string[] = [];
  displayedColumns: string[] = ['date', 'type', 'duration', 'nutritionist', 'book'];

  constructor(private appointmentService : AppointmentService, private router : Router, private loginService : LoginService,
    private userProfileService : UserProfileService){}

  ngOnInit(){
    this.getAvailableAppointments();
  }

  getAvailableAppointments(){
    this.appointmentService.getAvailableAppointments().subscribe(
      (data) =>{
        this.dataSource = data;
        for(let i =0; i< this.dataSource.length; i++){
          this.nutritionistFullName[i] = this.dataSource[i].nutritionistName + " " + this.dataSource[i].nutritionistLastName;
          if(this.dataSource[i].duration == 20){
            this.appointmentType[i] = 'CONSULTATION';
          }
          else if(this.dataSource[i].duration == 30){
            this.appointmentType[i] = 'CONTROL';
          }
          else if(this.dataSource[i].duration == 60){
            this.appointmentType[i] = 'EXAMINATION';
          }
        }
        console.log(this.dataSource);
        this.sortByDate();
      },
      (error) =>
        alert(error.message)
    );
  }

  bookAppointment(element: any) {
    var userDto : UserDto = {
      username: '',
      password: '',
      name: '',
      lastName: '',
      gender: 2,
      physicalActivity: 4,
      weight: 0,
      height: 0,
      age: 0,
      goal: 3,
      diseases: false,
      email: '',
      phoneNumber: '',
      address: ''
    };

    this.userProfileService.getUserInfo(this.loginService.getUserId()!).subscribe(
      (data) => {
        userDto = data;
        if(userDto.username == '' || userDto.password == '' || userDto.name == '' || userDto.lastName == '' || userDto.gender == 2
          || userDto.physicalActivity == 4 || userDto.weight == 0 || userDto.height == 0 || userDto.age ==0 || userDto.goal == 3
          || userDto.email == '' || userDto.phoneNumber == '' || userDto.address == ''){
          alert("Incomplete user data. Go to user profile and fill in all fields");
          return;
        } else{
          const dto : BookAppointmentDto = {
            appointmentId: element.appointmentId,
            patientId: this.loginService.getUserId()!
          };
      
          this.appointmentService.bookAppointment(dto).subscribe(
            (data) =>{
              alert("Appointment booked successfully");
              this.getAvailableAppointments();
            },
            (error) =>
              console.log(error.message)
          );
        }
      },
      (error) =>
        alert("Error loading user data")
    );
  }

  getOnlyExaminations() {
    this.appointmentService.getAvailableAppointments().subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 60);
        for(let i =0; i< this.dataSource.length; i++){
          this.nutritionistFullName[i] = this.dataSource[i].nutritionistName + " " + this.dataSource[i].nutritionistLastName;
          if(this.dataSource[i].duration == 20){
            this.appointmentType[i] = 'CONSULTATION';
          }
          else if(this.dataSource[i].duration == 30){
            this.appointmentType[i] = 'CONTROL';
          }
          else if(this.dataSource[i].duration == 60){
            this.appointmentType[i] = 'EXAMINATION';
          }
        }
      },
      (error) => {
        alert(error.message);
      }
    );
  }
  
  getOnlyControls() {
    this.appointmentService.getAvailableAppointments().subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 30);
        for(let i =0; i< this.dataSource.length; i++){
          this.nutritionistFullName[i] = this.dataSource[i].nutritionistName + " " + this.dataSource[i].nutritionistLastName;
          if(this.dataSource[i].duration == 20){
            this.appointmentType[i] = 'CONSULTATION';
          }
          else if(this.dataSource[i].duration == 30){
            this.appointmentType[i] = 'CONTROL';
          }
          else if(this.dataSource[i].duration == 60){
            this.appointmentType[i] = 'EXAMINATION';
          }
        }
      },
      (error) => {
        alert(error.message);
      }
    );
  }
  
  getOnlyConsultation() {
    this.appointmentService.getAvailableAppointments().subscribe(
      (data) => {
        this.dataSource = data.filter((dto: PatientAppointmentDto) => dto.duration === 20);
        for(let i =0; i< this.dataSource.length; i++){
          this.nutritionistFullName[i] = this.dataSource[i].nutritionistName + " " + this.dataSource[i].nutritionistLastName;
          if(this.dataSource[i].duration == 20){
            this.appointmentType[i] = 'CONSULTATION';
          }
          else if(this.dataSource[i].duration == 30){
            this.appointmentType[i] = 'CONTROL';
          }
          else if(this.dataSource[i].duration == 60){
            this.appointmentType[i] = 'EXAMINATION';
          }
        }
      },
      (error) => {
        alert(error.message);
      }
    );
  }

  sortByDate(){
          var temp;
          var listToSort : PatientAppointmentDto[] = [...this.dataSource];
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

}
