import { Component } from '@angular/core';
import { LoginService } from '../service/login.service';
import { LoginDto } from '../interfaces/LoginDto';
import { Router } from '@angular/router';


@Component({
  selector: 'app-login',
  standalone: false,
  
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor(private loginService : LoginService, private router : Router){}

  semafor : any;
  
  login(result: any) {
  if (result.username == '' || result.password == "") {
    alert("you must fill in all fields");
    return;
  }

  const dto: LoginDto = {
    username: result.username,
    password: result.password
  };

  this.loginService.login(dto).subscribe(
    (data) => {
      localStorage.setItem('JWT', data.accessToken);
      this.loginService.userLoginTracker();
      this.isBlocked();

      // Proveravamo rolu pre poziva `hasLogged`
      if (this.loginService.getRole() === 'PATIENT') {
        this.hasLogged(this.loginService.getUserId()!).then((semafor) => {
          if (!semafor) {
            this.router.navigate(['user-profile']);
          } else {
            this.router.navigate(['']);
          }
        });
      } else {
        this.router.navigate(['']);
      }
    },
    (error) => {
      alert(error);
    }
  );
}

async hasLogged(userId: number): Promise<boolean> {
  return new Promise((resolve, reject) => {
    this.loginService.getHasLogged(userId).subscribe(
      (data) => {
        this.semafor = data;
        console.log(this.loginService.getRole(), "rola");
        console.log(this.semafor, "semafor");
        resolve(this.semafor);
      },
      (error) => {
        alert(error);
        reject(false);
      }
    );
  });
}


  penaltyPoints(){
    this.loginService.getUsersPenaltyPoints(this.loginService.getUserId()!).subscribe(
      (data) =>{
        if(data >= 5){
          alert("User is banned");
          this.loginService.logout();
        }
       },
      (error) =>
         alert("Could not log in")
    );
  }

  isBlocked(){
    this.loginService.getIsBlocked(this.loginService.getUserId()!).subscribe(
      (data) =>{
        if(data == true){
          alert("User is banned");
          this.loginService.logout();
        }
       },
      (error) =>
         alert("Could not log in")
    );
  }


}
