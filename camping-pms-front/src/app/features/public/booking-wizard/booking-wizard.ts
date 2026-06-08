import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatStepperModule } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PublicNavbar } from '../../../shared/components/public-navbar/public-navbar';
import { PublicService } from '../../../core/services/public.service';
import { Accommodation } from '../../../core/services/accommodation.service';

@Component({
  selector: 'app-booking-wizard',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    PublicNavbar
  ],
  templateUrl: './booking-wizard.html',
  styleUrl: './booking-wizard.scss'
})
export class BookingWizard implements OnInit {
  accommodation: Accommodation | null = null;
  datesForm: FormGroup;
  personalForm: FormGroup;
  paymentForm: FormGroup;
  loading = false;
  availabilityChecked = false;
  isAvailable = false;
  nights = 0;
  totalPrice = 0;
  minDate = new Date();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private publicService: PublicService,
    private cdr: ChangeDetectorRef
  ) {
    this.datesForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      adults: [1, [Validators.required, Validators.min(1)]],
      children: [0, Validators.min(0)]
    });

    this.personalForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required]
    });

    this.paymentForm = this.fb.group({
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cardName: ['', Validators.required],
      expiry: ['', [Validators.required, Validators.pattern(/^\d{2}\/\d{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3}$/)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.publicService.getAccommodation(id).subscribe({
      next: data => {
        this.accommodation = data;
        this.cdr.detectChanges();
      }
    });

    this.datesForm.valueChanges.subscribe(() => {
      this.calculatePrice();
      this.cdr.detectChanges();
    });
  }

  calculatePrice(): void {
    const { startDate, endDate } = this.datesForm.value;
    if (startDate && endDate && this.accommodation) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      this.nights = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
      this.totalPrice = this.nights > 0 ? this.nights * this.accommodation.basePrice : 0;
    }
  }

  checkAvailability(): void {
    if (this.datesForm.invalid || !this.accommodation) return;
    this.loading = true;

    const { startDate, endDate } = this.datesForm.value;
    this.publicService.checkAvailability(
      this.accommodation.id,
      this.formatDate(startDate),
      this.formatDate(endDate)
    ).subscribe({
      next: result => {
        this.isAvailable = result.available;
        this.availabilityChecked = true;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  confirm(): void {
    if (!this.accommodation) return;
    this.loading = true;

    const request = {
      accommodationId: this.accommodation.id,
      startDate: this.formatDate(this.datesForm.value.startDate),
      endDate: this.formatDate(this.datesForm.value.endDate),
      adults: this.datesForm.value.adults,
      children: this.datesForm.value.children,
      ...this.personalForm.value
    };

    this.publicService.createBooking(request).subscribe({
      next: confirmation => {
        this.loading = false;
        this.router.navigate(['/confirmation'], { state: { confirmation } });
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  private formatDate(date: Date | string): string {
    if (typeof date === 'string') return date;
    const d = new Date(date);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }
}