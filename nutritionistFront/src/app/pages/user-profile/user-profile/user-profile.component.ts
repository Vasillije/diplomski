import { Component } from '@angular/core';
import { UserProfileService } from '../services/user-profile.service';
import { LoginService } from '../../login/service/login.service';
import { UserProfileDto } from '../interfaces/UserProfileDto';
import { GeneralInfoDto } from '../interfaces/GeneralInfoDto';
import { ContactInfoDto } from '../interfaces/ContactInfoDto';
import { HealthInfoDto } from '../interfaces/HealthInfoDto';
import { UserDto } from '../interfaces/UserDto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: false,
  
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent {
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

  diseasesOptions = [
    { value: 'YES', viewValue: 'Yes' },
    { value: 'NO', viewValue: 'No' }
  ];

  get diseasesValue(): string {
    return this.user.diseases ? "YES" : "NO";
  }

  user! : UserProfileDto;
  username : string = '';
  
  constructor(private userProfileService : UserProfileService, private loginService : LoginService, private router : Router){}

  ngOnInit(){
   this.loadUserInfo();
  }

  loadUserInfo(){
    this.userProfileService.getUserByUsername(this.loginService.getUsername()!).subscribe(
      (data) => {
        this.user = data;
        this.username = data.username;
      },
      (error) =>
        alert(error.message)
    );
  }
  

  changeContactInfo(result: any) {
    const dto : ContactInfoDto = {
      id : this.loginService.getUserId()!,
      email: result.email,
      phoneNumber: result.phone,
      address: result.address
    };

    console.log("ovo je dto", dto);

    this.userProfileService.changeContactInfo(dto).subscribe(
      (data) =>{
        console.log(data)
        alert("Contact info changed");
        this.loadUserInfo();
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
      id : this.loginService.getUserId()!,
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
  
  changeGeneralInfo(result: any) {
    console.log("id : ", this.loginService.getUserId()!)
    if(result.password !== result.retypedPassword){
      alert("Passwords dont match");
      return;
    }

    const dto : GeneralInfoDto = {
      id : this.loginService.getUserId()!,
      username: result.username,
      password: result.password,
      name: result.name,
      lastName: result.lastname
    };

    console.log("ovo je dto", dto);

    this.userProfileService.changeGeneralInfo(dto).subscribe(
      (data) =>{
        console.log(data)
        alert("General info changed");
        this.loadUserInfo();
      },
    (error) =>
      alert(error.message)
    );
  }

  finish() {
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
        console.log(userDto);
        if(userDto.username == '' || userDto.password == '' || userDto.name == '' || userDto.lastName == '' || userDto.gender == 2
          || userDto.physicalActivity == 4 || userDto.weight == 0 || userDto.height == 0 || userDto.age ==0 || userDto.goal == 3
          || userDto.email == '' || userDto.phoneNumber == '' || userDto.address == ''){
          alert("You must fill in all fields");
          return;
        } else{
          this.router.navigate(['']);
        }
      },
    (error) =>
      alert("error")
    );
  }

}
