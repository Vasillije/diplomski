import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { MaterialModule } from '../../material/material.module';
import { UserHealthInfoComponent } from './user-health-info/user-health-info.component';



@NgModule({
  declarations: [
    UserProfileComponent,
    UserHealthInfoComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    UserHealthInfoComponent
  ]
})
export class UserProfileModule { }
