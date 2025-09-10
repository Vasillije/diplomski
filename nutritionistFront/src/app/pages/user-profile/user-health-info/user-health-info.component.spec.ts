import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserHealthInfoComponent } from './user-health-info.component';

describe('UserHealthInfoComponent', () => {
  let component: UserHealthInfoComponent;
  let fixture: ComponentFixture<UserHealthInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserHealthInfoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserHealthInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
