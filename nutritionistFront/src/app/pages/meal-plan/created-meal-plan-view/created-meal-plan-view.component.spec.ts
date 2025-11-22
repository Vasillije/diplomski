import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatedMealPlanViewComponent } from './created-meal-plan-view.component';

describe('CreatedMealPlanViewComponent', () => {
  let component: CreatedMealPlanViewComponent;
  let fixture: ComponentFixture<CreatedMealPlanViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreatedMealPlanViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatedMealPlanViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
