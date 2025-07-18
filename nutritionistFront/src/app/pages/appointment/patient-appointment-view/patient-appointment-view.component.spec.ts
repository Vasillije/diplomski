import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientAppointmentViewComponent } from './patient-appointment-view.component';

describe('PatientAppointmentViewComponent', () => {
  let component: PatientAppointmentViewComponent;
  let fixture: ComponentFixture<PatientAppointmentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PatientAppointmentViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientAppointmentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
