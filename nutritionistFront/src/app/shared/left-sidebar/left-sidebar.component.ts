import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginService } from '../../pages/login/service/login.service';
import { UserProfileService } from '../../pages/user-profile/services/user-profile.service';
import { UserDto } from '../../pages/user-profile/interfaces/UserDto';
import { MealPlanService } from '../../pages/meal-plan/service/meal-plan.service';

interface MenuItem {
  routeLink: string;
  icon: string;
  label: string;
  action?: () => void;
}

@Component({
  selector: 'app-left-sidebar',
  standalone: false,
  
  templateUrl: './left-sidebar.component.html',
  styleUrl: './left-sidebar.component.css'
})
export class LeftSidebarComponent {
  constructor(private loginService : LoginService, private userProfileService : UserProfileService, private mealPlanService : MealPlanService){}

  isLeftSidebarCollapsed = input.required<boolean>();
  changeIsLeftSidebarCollapsed = output<boolean>();

  itemsAdmin: MenuItem[] = [
    {
      routeLink : 'unblock-user-view',
      icon: 'fas fa-unlock',
      label: 'Unblock users'
    },
    {
      routeLink : 'admin-complaint-and-answer-view',
      icon: 'fas fa-circle-exclamation',
      label: 'Complaints and answers'
    },
    {
      routeLink : 'block-nutritionist-view',
      icon: 'fas fa-ban',
      label: 'Block nutritionist'
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
    {
      routeLink : '',
      icon: 'fas fa-burger',
      label: 'Preliminary nutrition plan',
      action: () => this.makePreliminaryNutritionPlan()
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
  
  // Ako je role niz, uzmite prvi element
  const roleValue = Array.isArray(role) ? role[0] : role;
  
  switch(roleValue) {
    case 'ADMIN':
      return this.itemsAdmin;
    case 'NUTRITIONIST':
      return this.itemsNutritionist;
    case 'PATIENT':
      return this.itemsPatient;
    default:
      return this.itemsLoggedOut;
  }
}

makePreliminaryNutritionPlan(){
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
        } else {
          this.mealPlanService.generateMealPlan(this.loginService.getUserId()!).subscribe(
          (data) => {
            alert("Meal plan created")
          },
          (error) =>
            alert("Could not create meal plan")
          );
        }
      },
      (error) =>
        alert("An error occured")
    );
  }

  toggleCollapse(): void{
    this.changeIsLeftSidebarCollapsed.emit(!this.isLeftSidebarCollapsed());
  }

  closeSidenav(): void{
    this.changeIsLeftSidebarCollapsed.emit(true);
  }
}
