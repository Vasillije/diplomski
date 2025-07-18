import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComplaintAnswerViewComponent } from './complaint-answer-view.component';

describe('ComplaintAnswerViewComponent', () => {
  let component: ComplaintAnswerViewComponent;
  let fixture: ComponentFixture<ComplaintAnswerViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ComplaintAnswerViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComplaintAnswerViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
