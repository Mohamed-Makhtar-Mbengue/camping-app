import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AccommodationService, Accommodation } from '../../../core/services/accommodation.service';

@Component({
  selector: 'app-accommodation-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier' : 'Ajouter' }} un hébergement</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nom</mat-label>
          <input matInput formControlName="name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Type</mat-label>
          <mat-select formControlName="type">
            <mat-option value="TENTE">⛺ Tente</mat-option>
            <mat-option value="MOBIL_HOME">🏠 Mobil-home</mat-option>
            <mat-option value="CHALET">🏔️ Chalet</mat-option>
            <mat-option value="BUNGALOW">🏡 Bungalow</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Capacité (personnes)</mat-label>
          <input matInput type="number" formControlName="capacity">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Prix par nuit (€)</mat-label>
          <input matInput type="number" formControlName="basePrice">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="3"></textarea>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Annuler</button>
      <button mat-raised-button color="primary"
              (click)="save()" [disabled]="form.invalid">
        {{ data ? 'Modifier' : 'Ajouter' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form { display: flex; flex-direction: column; gap: 8px; padding-top: 8px; }
    .full-width { width: 100%; }
  `]
})
export class AccommodationForm {
  form: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<AccommodationForm>,
    @Inject(MAT_DIALOG_DATA) public data: Accommodation | null,
    private fb: FormBuilder,
    private accommodationService: AccommodationService
  ) {
    this.form = this.fb.group({
      name: [data?.name || '', Validators.required],
      type: [data?.type || '', Validators.required],
      capacity: [data?.capacity || 1, [Validators.required, Validators.min(1)]],
      basePrice: [data?.basePrice || 0, [Validators.required, Validators.min(0)]],
      description: [data?.description || '']
    });
  }

  save(): void {
    if (this.form.invalid) return;
    const obs = this.data
      ? this.accommodationService.update(this.data.id, this.form.value)
      : this.accommodationService.create(this.form.value);

    obs.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false)
    });
  }
}