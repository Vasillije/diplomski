import { Component, HostListener, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from './pages/login/service/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'nutritionistFront';
  isLeftSidebarCollapsed = signal<boolean>(false);
  screenWidth = signal<number>(window.innerWidth);

  constructor(private router : Router, public loginService : LoginService){}

  @HostListener('window:resize')
  onResize(){
    this.screenWidth.set(window.innerWidth);
    if(this.screenWidth() < 768){
      this.isLeftSidebarCollapsed.set(true);
    }
  }

  ngOnInit(){
    this.loginService.userLoginTracker();
    this.isLeftSidebarCollapsed.set(this.screenWidth() < 768);
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

  changeIsLeftSidebarCollapsed(isLeftSidebarCollapsed: boolean): void {
    this.isLeftSidebarCollapsed.set(isLeftSidebarCollapsed);
  }

}
