import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnsweredNutritionistComplaintsViewComponent } from './answered-nutritionist-complaints-view.component';

describe('AnsweredNutritionistComplaintsViewComponent', () => {
  let component: AnsweredNutritionistComplaintsViewComponent;
  let fixture: ComponentFixture<AnsweredNutritionistComplaintsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AnsweredNutritionistComplaintsViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnsweredNutritionistComplaintsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
