import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAppointmentViewComponent } from './create-appointment-view.component';

describe('CreateAppointmentViewComponent', () => {
  let component: CreateAppointmentViewComponent;
  let fixture: ComponentFixture<CreateAppointmentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateAppointmentViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateAppointmentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
