import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeftSidebarComponent } from './left-sidebar/left-sidebar.component';
import { MaterialModule } from '../material/material.module';
import { MainComponent } from './main/main.component';
import { RouterModule } from '@angular/router';


@NgModule({
  declarations: [
    LeftSidebarComponent,
    MainComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule
  ],
  exports: [
    LeftSidebarComponent,
    MainComponent,
    RouterModule
  ]
})
export class SharedModule { }
