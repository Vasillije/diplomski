import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateMealPlanPatientViewComponent } from './create-meal-plan-patient-view.component';

describe('CreateMealPlanPatientViewComponent', () => {
  let component: CreateMealPlanPatientViewComponent;
  let fixture: ComponentFixture<CreateMealPlanPatientViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateMealPlanPatientViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateMealPlanPatientViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
