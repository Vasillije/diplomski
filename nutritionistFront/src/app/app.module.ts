import { MaterialModule } from './material/material.module';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginModule } from './pages/login/login.module';
import { RegisterModule } from './pages/register/register.module';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HomepageModule } from './pages/homepage/homepage.module';
import { provideHttpClient } from '@angular/common/http';
import { UserProfileModule } from './pages/user-profile/user-profile.module';
import { AppointmentModule } from './pages/appointment/appointment.module';
import { ComplaintModule } from './pages/complaint/complaint.module';
import { AdminModule } from './pages/admin/admin.module';
import { MealPlanModule } from './pages/meal-plan/meal-plan.module';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    MaterialModule,
    BrowserModule,
    AppRoutingModule,
    LoginModule,
    RegisterModule,
    HomepageModule,
    UserProfileModule,
    AppointmentModule,
    ComplaintModule,
    AdminModule,
    MealPlanModule,
    SharedModule
  ],
  providers: [
    provideAnimationsAsync(),
    provideHttpClient()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
