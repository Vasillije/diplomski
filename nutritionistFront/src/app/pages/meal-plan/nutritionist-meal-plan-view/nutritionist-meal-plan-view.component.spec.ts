import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NutritionistMealPlanViewComponent } from './nutritionist-meal-plan-view.component';

describe('NutritionistMealPlanViewComponent', () => {
  let component: NutritionistMealPlanViewComponent;
  let fixture: ComponentFixture<NutritionistMealPlanViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NutritionistMealPlanViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NutritionistMealPlanViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
