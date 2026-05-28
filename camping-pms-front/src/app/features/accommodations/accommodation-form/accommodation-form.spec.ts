import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccommodationForm } from './accommodation-form';

describe('AccommodationForm', () => {
  let component: AccommodationForm;
  let fixture: ComponentFixture<AccommodationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccommodationForm],
    }).compileComponents();

    fixture = TestBed.createComponent(AccommodationForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
