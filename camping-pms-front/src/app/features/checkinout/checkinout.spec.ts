import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Checkinout } from './checkinout';

describe('Checkinout', () => {
  let component: Checkinout;
  let fixture: ComponentFixture<Checkinout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Checkinout],
    }).compileComponents();

    fixture = TestBed.createComponent(Checkinout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
