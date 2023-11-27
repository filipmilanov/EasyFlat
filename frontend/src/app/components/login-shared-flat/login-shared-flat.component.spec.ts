import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginSharedFlatComponent } from './login-shared-flat.component';

describe('LoginSharedFlatComponent', () => {
  let component: LoginSharedFlatComponent;
  let fixture: ComponentFixture<LoginSharedFlatComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginSharedFlatComponent]
    });
    fixture = TestBed.createComponent(LoginSharedFlatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
