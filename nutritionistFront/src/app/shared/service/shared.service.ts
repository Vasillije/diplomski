import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserProfileService } from '../../pages/user-profile/services/user-profile.service';
import { LoginService } from '../../pages/login/service/login.service';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  // BehaviorSubject čuva trenutnu vrednost i emituje je novim pretplatnicima
  private hasPreliminaryPlanSubject = new BehaviorSubject<boolean>(false);
  
  // Public Observable koji komponente mogu da slušaju
  public hasPreliminaryPlan$: Observable<boolean> = this.hasPreliminaryPlanSubject.asObservable();
  
  constructor(private userProfileService: UserProfileService, private loginService: LoginService) {
    this.loadInitialState();
  }

  private loadInitialState(): void {
    const userId = this.loginService.getUserId();
    if (userId) {
      this.userProfileService.hasPreliminaryNutritionPlan(userId).subscribe(
        (hasPlan) => {
          this.setHasPreliminaryPlan(hasPlan);
        },
        (error) => {
          console.error('Error loading preliminary plan state:', error);
          this.setHasPreliminaryPlan(false);
        }
      );
    }
  }

  setHasPreliminaryPlan(value: boolean): void {
    console.log('Servis: Promena vrednosti na', value);
    this.hasPreliminaryPlanSubject.next(value);
  }

  getCurrentValue(): boolean {
    return this.hasPreliminaryPlanSubject.value;
  }
}
