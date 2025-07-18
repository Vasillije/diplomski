import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateMealPlanViewComponent } from './create-meal-plan-view.component';

describe('CreateMealPlanViewComponent', () => {
  let component: CreateMealPlanViewComponent;
  let fixture: ComponentFixture<CreateMealPlanViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateMealPlanViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateMealPlanViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
