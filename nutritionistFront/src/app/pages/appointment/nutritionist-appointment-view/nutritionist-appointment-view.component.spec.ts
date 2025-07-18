import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NutritionistAppointmentViewComponent } from './nutritionist-appointment-view.component';

describe('NutritionistAppointmentViewComponent', () => {
  let component: NutritionistAppointmentViewComponent;
  let fixture: ComponentFixture<NutritionistAppointmentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NutritionistAppointmentViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NutritionistAppointmentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
