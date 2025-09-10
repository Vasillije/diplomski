import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComplaintViewComponent } from './complaint-view/complaint-view.component';
import { MaterialModule } from '../../material/material.module';
import { NutritionistComplaintsViewComponent } from './nutritionist-complaints-view/nutritionist-complaints-view.component';
import { ComplaintAnswerViewComponent } from './complaint-answer-view/complaint-answer-view.component';
import { AnsweredNutritionistComplaintsViewComponent } from './answered-nutritionist-complaints-view/answered-nutritionist-complaints-view.component';



@NgModule({
  declarations: [
    ComplaintViewComponent,
    NutritionistComplaintsViewComponent,
    ComplaintAnswerViewComponent,
    AnsweredNutritionistComplaintsViewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ]
})
export class ComplaintModule { }
