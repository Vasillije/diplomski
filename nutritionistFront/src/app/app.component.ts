import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from './pages/login/service/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'nutritionistFront';

  constructor(private router : Router, public loginService : LoginService){}

  ngOnInit(){
    this.loginService.userLoginTracker();
  }

  logout() {
    this.loginService.logout();
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  redirectToRegister() {
    this.router.navigate(['register']);
  }
}
