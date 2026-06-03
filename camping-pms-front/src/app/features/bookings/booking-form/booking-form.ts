import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../../core/services/booking';
import { AccommodationService, Accommodation } from '../../../core/services/accommodation.service';

@Component({
  selector: 'app-booking-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  template: `
    <h2 mat-dialog-title>Nouvelle réservation</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Hébergement</mat-label>
          <mat-select formControlName="accommodationId">
            @for (acc of accommodations; track acc.id) {
              <mat-option [value]="acc.id">
                {{ acc.name }} — {{ acc.type }} — {{ acc.basePrice }}€/nuit
              </mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Date d'arrivée</mat-label>
          <input matInput [matDatepicker]="startPicker" formControlName="startDate">
          <mat-datepicker-toggle matSuffix [for]="startPicker" />
          <mat-datepicker #startPicker />
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Date de départ</mat-label>
          <input matInput [matDatepicker]="endPicker" formControlName="endDate">
          <mat-datepicker-toggle matSuffix [for]="endPicker" />
          <mat-datepicker #endPicker />
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Adultes</mat-label>
          <input matInput type="number" formControlName="adults" min="1">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Enfants</mat-label>
          <input matInput type="number" formControlName="children" min="0">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Annuler</button>
      <button mat-raised-button color="primary"
              (click)="save()" [disabled]="form.invalid || saving">
        {{ saving ? 'En cours...' : 'Réserver' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form { display: flex; flex-direction: column; gap: 8px; padding-top: 8px; min-width: 400px; }
    .full-width { width: 100%; }
  `]
})
export class BookingForm implements OnInit {
  form: FormGroup;
  accommodations: Accommodation[] = [];
  saving = false;

  constructor(
    public dialogRef: MatDialogRef<BookingForm>,
    private fb: FormBuilder,
    private bookingService: BookingService,
    private accommodationService: AccommodationService,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      accommodationId: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      adults: [1, [Validators.required, Validators.min(1)]],
      children: [0, Validators.min(0)]
    });
  }

  ngOnInit(): void {
    this.accommodationService.getAll(0, 100).subscribe({
      next: data => {
        this.accommodations = data.content;
        this.cdr.detectChanges();
      }
    });
  }

  save(): void {
    if (this.form.invalid) return;
    this.saving = true;

    const formValue = this.form.value;
    const request = {
      accommodationId: formValue.accommodationId,
      startDate: this.formatDate(formValue.startDate),
      endDate: this.formatDate(formValue.endDate),
      adults: formValue.adults,
      children: formValue.children
    };

    this.bookingService.create(request).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => {
        this.saving = false;
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