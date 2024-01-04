import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HaushaltPlanComponent } from './haushalt-plan.component';

describe('HaushaltPlanComponent', () => {
  let component: HaushaltPlanComponent;
  let fixture: ComponentFixture<HaushaltPlanComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HaushaltPlanComponent]
    });
    fixture = TestBed.createComponent(HaushaltPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
