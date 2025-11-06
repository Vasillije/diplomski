import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { LoginService } from '../pages/login/service/login.service';
import { map, take } from 'rxjs/operators';
import { SharedService } from '../shared/service/shared.service';
import { UserProfileService } from '../pages/user-profile/services/user-profile.service';

@Injectable({
  providedIn: 'root'
})
export class PreliminaryPlanGuard implements CanActivate {
  
  constructor(private sharedService: SharedService, private loginService: LoginService, private router: Router, private userProfileService: UserProfileService) {}

  canActivate(route: ActivatedRouteSnapshot) {
  const userId = this.loginService.getUserId();
  const role = this.loginService.getRole();
  
  const isPatient = Array.isArray(role) 
    ? role.includes('PATIENT') 
    : role === 'PATIENT';
  
  if (!userId || !isPatient) {
    this.router.navigate(['/login']);
    return false;
  }

  // Uvek proveri direktno sa serverom - zanemari servis
  return this.userProfileService.hasPreliminaryNutritionPlan(userId).pipe(
    map(hasPlan => {
      if (hasPlan) {
        alert('You already have a preliminary nutrition plan!');
        this.router.navigate(['/']);
        return false;
      }
      return true;
    })
  );
}
}