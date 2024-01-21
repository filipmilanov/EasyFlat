import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChoreCardComponent } from './my-chore-card.component';

describe('MyChoreCardComponent', () => {
  let component: MyChoreCardComponent;
  let fixture: ComponentFixture<MyChoreCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyChoreCardComponent]
    });
    fixture = TestBed.createComponent(MyChoreCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
