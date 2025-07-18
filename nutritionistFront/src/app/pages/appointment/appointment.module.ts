import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateAppointmentComponent } from './create-appointment/create-appointment.component';
import { MaterialModule } from '../../material/material.module';
import { NutritionistAppointmentViewComponent } from './nutritionist-appointment-view/nutritionist-appointment-view.component';
import { PatientAppointmentViewComponent } from './patient-appointment-view/patient-appointment-view.component';
import { BookAppointmentViewComponent } from './book-appointment-view/book-appointment-view.component';



@NgModule({
  declarations: [
    CreateAppointmentComponent,
    NutritionistAppointmentViewComponent,
    PatientAppointmentViewComponent,
    BookAppointmentViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ]
})
export class AppointmentModule { }
