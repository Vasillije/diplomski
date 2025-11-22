import { Component, input, output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginService } from '../../pages/login/service/login.service';
import { UserProfileService } from '../../pages/user-profile/services/user-profile.service';
import { UserDto } from '../../pages/user-profile/interfaces/UserDto';
import { MealPlanService } from '../../pages/meal-plan/service/meal-plan.service';
import { SharedService } from '../service/shared.service';
import { Subscription } from 'rxjs';

interface MenuItem {
  routeLink: string;
  icon: string;
  label: string;
  action?: () => void;
  showCondition?: boolean;
}

@Component({
  selector: 'app-left-sidebar',
  standalone: false,
  
  templateUrl: './left-sidebar.component.html',
  styleUrl: './left-sidebar.component.css'
})
export class LeftSidebarComponent {
  constructor(private loginService : LoginService, private userProfileService : UserProfileService, private mealPlanService : MealPlanService,
    private cdr: ChangeDetectorRef, private sharedService : SharedService
  ){}

  isLeftSidebarCollapsed = input.required<boolean>();
  changeIsLeftSidebarCollapsed = output<boolean>();
  private subscription: Subscription | undefined;

  hasPreliminaryPlan : boolean = false;

  ngOnInit(){
    //this.hasPreliminaryNutritionPlan();
    this.subscription = this.sharedService.hasPreliminaryPlan$.subscribe(
      (newValue) => {
        this.hasPreliminaryPlan = newValue;
      }
    );
  }

  hasPreliminaryNutritionPlan(){
    this.userProfileService.hasPreliminaryNutritionPlan(this.loginService.getUserId()!).subscribe(
      (data) =>{
        this.hasPreliminaryPlan = data;
        this.cdr.detectChanges(); // Osveži view
      },
      (error) => {
        console.error("Error checking preliminary plan:", error);
        this.hasPreliminaryPlan = false;
        this.cdr.detectChanges(); // Osveži view
      }
    );
  }

  get preliminaryPlanRoute(): string {
    return `preliminary-nutrition-plan-view/${this.loginService.getUserId()}`;
}


  itemsAdmin: MenuItem[] = [
    {
      routeLink : 'unblock-user-view',
      icon: 'fas fa-unlock',
      label: '(Un)block users'
    },
    {
      routeLink : 'admin-complaint-and-answer-view',
      icon: 'fas fa-circle-exclamation',
      label: 'Complaints and answers'
    },
    {
      routeLink: '',
      icon: 'fas fa-sign-out-alt',
      label: 'Logout',
      action: () => this.loginService.logout()
    }
  ];

  itemsNutritionist: MenuItem[] = [
    {
      routeLink : 'create-meal-plan-view',
      icon: 'fas fa-burger',
      label: 'Meal plan'
    },
    {
      routeLink : 'created-meal-plan-view',
      icon: 'fas fa-check',
      label: 'Created meal plans'
    },
    {
      routeLink : 'nutritionist-complaints-view',
      icon: 'fas fa-message',
      label: 'Complaints'
    },
    {
      routeLink : 'nutritionist-appointment-view',
      icon: 'fas fa-calendar-check',
      label: 'Created appointments'
    },
    {
      routeLink : 'book-appointment-nutritionist-view',
      icon: 'fas fa-circle-plus',
      label: 'Book appointment'
    },
    {
      routeLink : 'create-appointment-view',
      icon: 'fas fa-plus',
      label: 'Create appointment'
    },
    {
      routeLink: '',
      icon: 'fas fa-sign-out-alt',
      label: 'Logout',
      action: () => this.loginService.logout()
    }
  ];

  itemsPatient: MenuItem[] = [
    {
      routeLink : 'patient-appointment-view',
      icon: 'fas fa-calendar-days',
      label: 'Scheduled appointments'
    },
    {
      routeLink : 'book-appointment-view',
      icon: 'fas fa-circle-plus',
      label: 'Book appointment'
    },
    {
      routeLink : 'user-profile',
      icon: 'fas fa-user',
      label: 'Profile'
    },
    { // jos treba da izmenis da na klik dugmeta kreira preliminary nutrition plan i da updatuje healt info 
      routeLink : `preliminary-nutrition-plan-view`,///${this.loginService.getUserId()}
      icon: 'fas fa-burger',
      label: 'Preliminary nutrition plan'
    },
    {
      routeLink: '',
      icon: 'fas fa-sign-out-alt',
      label: 'Logout',
      action: () => this.loginService.logout()
    }
  ];

  itemsLoggedOut: MenuItem[] = [
    {
      routeLink : '',
      icon: 'fas fa-home',
      label: 'Homepage'
    },
    {
      routeLink : 'login',
      icon: 'fas fa-right-to-bracket',
      label: 'Login'
    },
    {
      routeLink : 'register',
      icon: 'fas fa-registered',
      label: 'Register'
    }
  ];

  getItemsBasedOnRole() {
    const role = this.loginService.getRole();
    const roleValue = Array.isArray(role) ? role[0] : role;
    
    switch(roleValue) {
      case 'ADMIN':
        return this.itemsAdmin;
      case 'NUTRITIONIST':
        return this.itemsNutritionist;
      case 'PATIENT':
        return this.getFilteredPatientItems(); // Koristi filtriranu listu
      default:
        return this.itemsLoggedOut;
    }
  }

  getFilteredPatientItems(): MenuItem[] {
    // Vrati sve iteme osim "Preliminary nutrition plan" ako hasPreliminaryPlan je true
    return this.itemsPatient.filter(item => {
      if (item.label === 'Preliminary nutrition plan' && this.hasPreliminaryPlan) {
        return false; // Sakrij ovaj item
      }
      return true; // Prikaži sve ostale iteme
    });
  }

  makePreliminaryNutritionPlan(){
    const userConfirmation = confirm("NOTE: You can get preliminary nutrition plan only once. Are you sure you want to proceed?");
    
    if (!userConfirmation) {
      return;
    }
    
    this.userProfileService.getUserInfo(this.loginService.getUserId()!).subscribe(
      (data) => {
        const userDto = data;
        if(userDto.username == '' || userDto.password == '' || userDto.name == '' || 
          userDto.lastName == '' || userDto.gender == 2 || userDto.physicalActivity == 4 || 
          userDto.weight == 0 || userDto.height == 0 || userDto.age == 0 || userDto.goal == 3 ||
          userDto.email == '' || userDto.phoneNumber == '' || userDto.address == ''){
          alert("Incomplete user data. Go to user profile and fill in all fields");
          return;
        } else {
          this.mealPlanService.generateMealPlan(this.loginService.getUserId()!).subscribe(
            (data) => {
              alert("Meal plan created");
              // Postavi flag i osveži prikaz
              this.hasPreliminaryPlan = true;
              this.cdr.detectChanges(); // Osveži view da bi sakrio dugme
            },
            (error) => {
              alert("Could not create meal plan");
            }
          );
        }
      },
      (error) => {
        alert("An error occurred");
      }
    );
  }

  toggleCollapse(): void{
    this.changeIsLeftSidebarCollapsed.emit(!this.isLeftSidebarCollapsed());
  }

  closeSidenav(): void{
    this.changeIsLeftSidebarCollapsed.emit(true);
  }
}
