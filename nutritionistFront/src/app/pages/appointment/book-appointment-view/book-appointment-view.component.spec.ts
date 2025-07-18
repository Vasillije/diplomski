import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookAppointmentViewComponent } from './book-appointment-view.component';

describe('BookAppointmentViewComponent', () => {
  let component: BookAppointmentViewComponent;
  let fixture: ComponentFixture<BookAppointmentViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BookAppointmentViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookAppointmentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
