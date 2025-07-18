import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login/login.component';
import { RegisterComponent } from './pages/register/register/register.component';
import { HomepageComponent } from './pages/homepage/homepage/homepage.component';
import { UserProfileComponent } from './pages/user-profile/user-profile/user-profile.component';
import { CreateAppointmentComponent } from './pages/appointment/create-appointment/create-appointment.component';
import { NutritionistAppointmentViewComponent } from './pages/appointment/nutritionist-appointment-view/nutritionist-appointment-view.component';
import { PatientAppointmentViewComponent } from './pages/appointment/patient-appointment-view/patient-appointment-view.component';
import { BookAppointmentViewComponent } from './pages/appointment/book-appointment-view/book-appointment-view.component';
import { ComplaintViewComponent } from './pages/complaint/complaint-view/complaint-view.component';
import { NutritionistComplaintsViewComponent } from './pages/complaint/nutritionist-complaints-view/nutritionist-complaints-view.component';
import { ComplaintAnswerViewComponent } from './pages/complaint/complaint-answer-view/complaint-answer-view.component';
import { BlockNutritionistViewComponent } from './pages/admin/block-nutritionist-view/block-nutritionist-view.component';
import { AdminComplaintAndAnswerViewComponent } from './pages/admin/admin-complaint-and-answer-view/admin-complaint-and-answer-view.component';
import { UnblockUserViewComponent } from './pages/admin/unblock-user-view/unblock-user-view.component';
import { NutritionistMealPlanViewComponent } from './pages/meal-plan/nutritionist-meal-plan-view/nutritionist-meal-plan-view.component';
import { CreateMealPlanViewComponent } from './pages/meal-plan/create-meal-plan-view/create-meal-plan-view.component';
import { CreateMealPlanPatientViewComponent } from './pages/meal-plan/create-meal-plan-patient-view/create-meal-plan-patient-view.component';
const routes: Routes = [
  { path : 'login', component : LoginComponent },
  { path : 'register', component : RegisterComponent },
  { path : '', component : HomepageComponent },
  { path : 'user-profile', component : UserProfileComponent},
  { path : 'create-appointment', component : CreateAppointmentComponent },
  { path : 'nutritionist-appointment-view', component : NutritionistAppointmentViewComponent },
  { path : 'patient-appointment-view', component : PatientAppointmentViewComponent },
  { path : 'book-appointment-view', component : BookAppointmentViewComponent },
  { path : 'complaint-view/:id', component : ComplaintViewComponent },
  { path : 'nutritionist-complaints-view', component : NutritionistComplaintsViewComponent }, 
  { path : 'complaint-answer-view/:id', component : ComplaintAnswerViewComponent },
  { path : 'block-nutritionist-view', component : BlockNutritionistViewComponent },
  { path : 'admin-complaint-and-answer-view', component : AdminComplaintAndAnswerViewComponent },
  { path : 'unblock-user-view', component : UnblockUserViewComponent },
  { path : 'create-meal-plan-view', component : CreateMealPlanViewComponent },
  { path : 'create-meal-plan-patient-view/:id', component : CreateMealPlanPatientViewComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
