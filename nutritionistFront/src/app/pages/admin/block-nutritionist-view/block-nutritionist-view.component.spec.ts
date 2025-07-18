import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockNutritionistViewComponent } from './block-nutritionist-view.component';

describe('BlockNutritionistViewComponent', () => {
  let component: BlockNutritionistViewComponent;
  let fixture: ComponentFixture<BlockNutritionistViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BlockNutritionistViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockNutritionistViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
