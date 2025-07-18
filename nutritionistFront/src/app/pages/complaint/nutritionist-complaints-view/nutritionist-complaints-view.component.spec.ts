import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NutritionistComplaintsViewComponent } from './nutritionist-complaints-view.component';

describe('NutritionistComplaintsViewComponent', () => {
  let component: NutritionistComplaintsViewComponent;
  let fixture: ComponentFixture<NutritionistComplaintsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NutritionistComplaintsViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NutritionistComplaintsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
