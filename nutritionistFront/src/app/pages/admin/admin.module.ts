import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BlockNutritionistViewComponent } from './block-nutritionist-view/block-nutritionist-view.component';
import { MaterialModule } from '../../material/material.module';
import { AdminComplaintAndAnswerViewComponent } from './admin-complaint-and-answer-view/admin-complaint-and-answer-view.component';
import { UnblockUserViewComponent } from './unblock-user-view/unblock-user-view.component';



@NgModule({
  declarations: [
    BlockNutritionistViewComponent,
    AdminComplaintAndAnswerViewComponent,
    UnblockUserViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ]
})
export class AdminModule { }
