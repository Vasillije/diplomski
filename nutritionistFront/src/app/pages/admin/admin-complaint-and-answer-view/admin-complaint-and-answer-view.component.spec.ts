import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminComplaintAndAnswerViewComponent } from './admin-complaint-and-answer-view.component';

describe('AdminComplaintAndAnswerViewComponent', () => {
  let component: AdminComplaintAndAnswerViewComponent;
  let fixture: ComponentFixture<AdminComplaintAndAnswerViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminComplaintAndAnswerViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminComplaintAndAnswerViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
