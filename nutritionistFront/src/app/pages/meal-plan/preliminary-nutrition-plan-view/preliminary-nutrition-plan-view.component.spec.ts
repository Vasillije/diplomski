import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreliminaryNutritionPlanViewComponent } from './preliminary-nutrition-plan-view.component';

describe('PreliminaryNutritionPlanViewComponent', () => {
  let component: PreliminaryNutritionPlanViewComponent;
  let fixture: ComponentFixture<PreliminaryNutritionPlanViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreliminaryNutritionPlanViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreliminaryNutritionPlanViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
