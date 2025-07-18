import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComplaintViewComponent } from './complaint-view/complaint-view.component';
import { MaterialModule } from '../../material/material.module';
import { NutritionistComplaintsViewComponent } from './nutritionist-complaints-view/nutritionist-complaints-view.component';
import { ComplaintAnswerViewComponent } from './complaint-answer-view/complaint-answer-view.component';



@NgModule({
  declarations: [
    ComplaintViewComponent,
    NutritionistComplaintsViewComponent,
    ComplaintAnswerViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ]
})
export class ComplaintModule { }
