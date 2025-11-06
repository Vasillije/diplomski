import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookAppointmentNutritionistViewComponent } from './book-appointment-nutritionist-view.component';

describe('BookAppointmentNutritionistViewComponent', () => {
  let component: BookAppointmentNutritionistViewComponent;
  let fixture: ComponentFixture<BookAppointmentNutritionistViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BookAppointmentNutritionistViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookAppointmentNutritionistViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
