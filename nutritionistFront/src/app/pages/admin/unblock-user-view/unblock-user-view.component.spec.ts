import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnblockUserViewComponent } from './unblock-user-view.component';

describe('UnblockUserViewComponent', () => {
  let component: UnblockUserViewComponent;
  let fixture: ComponentFixture<UnblockUserViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnblockUserViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnblockUserViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
