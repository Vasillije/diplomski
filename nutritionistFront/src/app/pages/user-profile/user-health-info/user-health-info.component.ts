import { Component } from '@angular/core';
import { UserProfileDto } from '../interfaces/UserProfileDto';
import { UserProfileService } from '../services/user-profile.service';
import { LoginService } from '../../login/service/login.service';
import { HealthInfoDto } from '../interfaces/HealthInfoDto';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientAppointmentDto } from '../../appointment/interfaces/PatientAppointmentDto';
import { PatientInfoDto } from '../interfaces/PatientInfoDto';

@Component({
  selector: 'app-user-health-info',
  standalone: false,
  
  templateUrl: './user-health-info.component.html',
  styleUrl: './user-health-info.component.css'
})
export class UserHealthInfoComponent {

  constructor(private userProfileService : UserProfileService, private loginService : LoginService, private route : ActivatedRoute
    , private router : Router
  ){}

  user! : PatientInfoDto;
  username : string = '';
  patientId! : number;

  genderOptions = [
    { value: 'MALE', viewValue: 'Male' },
    { value: 'FEMALE', viewValue: 'Female' },
    { value: 'EMPTY', viewValue: 'Empty'}
  ];

  activityOptions = [
    { value: 'NONE', viewValue: 'None' },
    { value: 'LOW', viewValue: 'Low' },
    { value: 'MEDIUM', viewValue: 'Medium' },
    { value: 'HIGH', viewValue: 'High' },
    { value: 'EMPTY', viewValue: 'Empty'}
  ];
  
  goalOptions = [
    { value: 'LOOSE_WEIGHT', viewValue: 'Loose weight' },
    { value: 'HEALTH', viewValue: 'Health' },
    { value: 'GAIN_WEIGHT', viewValue: 'Gain weight'},
    { value: 'EMPTY', viewValue: 'Empty'}
  ];

  ngOnInit(){
    this.patientId = +this.route.snapshot.paramMap.get('id')!;
    this.loadUserInfo();
  }

  loadUserInfo(){
    this.userProfileService.getPatientsInfo(this.patientId).subscribe(
      (data) => {
        this.user = data;
        this.patientId = this.user.patientId;
        console.log(data);
      },
      (error) =>
        alert(error.message)
    );
  }


  changeHealthInfo(result: any) {
      
      let gender: number = -1;
      let physicalActivity: number = -1;
      let goal: number= -1;
      let diseases: boolean = false;
  
      if(result.goal == "LOOSE_WEIGHT"){
        goal = 0;
      } 
      else if(result.goal == "HEALTH"){
        goal = 1;
      }
      else if(result.goal == "GAIN_WEIGHT"){
        goal = 2;
      }
      else if(result.goal == "EMPTY"){
        goal = 3;
      }
  
      if (result.gender == "MALE") {
        gender = 0;
      } 
      else if (result.gender == "FEMALE") {
        gender = 1;
      }
      else if (result.gender == "EMPTY") {
        gender = 2;
      }
  
      if (result.physicalActivity == "NONE") {
        physicalActivity = 0;
      } 
      else if (result.physicalActivity == "LOW") {
        physicalActivity = 1;
      }
      else if (result.physicalActivity == "MEDIUM") {
        physicalActivity = 2;
      } 
      else if (result.physicalActivity == "HIGH") {
        physicalActivity = 3;
      }
      else if (result.physicalActivity == "EMPTY") {
        physicalActivity = 4;
      }
  
      if (result.diseases == "YES") {
        diseases = true;
      } 
      else if (result.diseases == "NO") {
        diseases = false;
      }
  
      const dto : HealthInfoDto = {
        id : this.patientId,
        gender: gender,
        physicalActivity: physicalActivity,
        weight: result.weight,
        height: result.height,
        age: result.age,
        goal: goal,
        diseases: diseases
      };
  
      if(dto.weight < 30){
        alert("Weight must be bigger than 30")
        return;
      }
  
      if(dto.height < 100){
        alert("Height must be bigger than 100 cm")
        return;
      }
  
      if(dto.age < 13){
        alert("Age must be bigger than 13")
        return;
      }
  
      this.userProfileService.changeHealthInfo(dto).subscribe(
        (data) =>{
          console.log("Promenjeni podaci:", data);
          alert("Health info changed");
          this.loadUserInfo();
        },
      (error) =>
        alert(error.message)
      );
    }

    cancel(){
      this.router.navigate(['/create-meal-plan-view']);
    }

}
