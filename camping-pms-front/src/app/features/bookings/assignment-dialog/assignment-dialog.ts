import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface AccommodationSuggestion {
  id: string;
  name: string;
  type: string;
  category: string;
  capacity: number;
  basePrice: number;
}

@Component({
  selector: 'app-assignment-dialog',
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatRadioModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>
      🏕️ Assigner un emplacement
    </h2>
    <mat-dialog-content>
      <p class="booking-info">
        <strong>Réservation :</strong> {{ data.accommodationName }}<br>
        <strong>Dates :</strong> {{ data.startDate }} → {{ data.endDate }}<br>
        <strong>Personnes :</strong> {{ data.adults }} adulte(s) + {{ data.children }} enfant(s)
      </p>

      @if (loading) {
        <div class="loading"><mat-spinner diameter="40" /></div>
      } @else if (suggestions.length === 0) {
        <div class="no-suggestions">
          <mat-icon>warning</mat-icon>
          <p>Aucun emplacement disponible pour ces critères.</p>
        </div>
      } @else {
        <p class="subtitle">
          Sélectionnez un emplacement parmi les
          <strong>{{ suggestions.length }}</strong> disponibles :
        </p>
        <div class="suggestions-list">
          @for (acc of suggestions; track acc.id) {
            <div class="suggestion-item"
                 [class.selected]="selectedId === acc.id"
                 (click)="selectedId = acc.id">
              <mat-radio-button [value]="acc.id" [(ngModel)]="selectedId">
                <div class="suggestion-info">
                  <span class="name">{{ acc.name }}</span>
                  <span class="details">
                    {{ acc.category }} — {{ acc.capacity }} pers. — {{ acc.basePrice }}€/nuit
                  </span>
                </div>
              </mat-radio-button>
            </div>
          }
        </div>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="dialogRef.close()">Annuler</button>
      <button mat-raised-button color="primary"
              (click)="confirm()"
              [disabled]="!selectedId || confirming">
        @if (confirming) { <mat-spinner diameter="20" /> }
        @else {
        <ng-container>
            <mat-icon>check</mat-icon> Confirmer la réservation
        </ng-container>
        }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .booking-info {
      background: #f5f7fa;
      padding: 12px;
      border-radius: 8px;
      margin-bottom: 16px;
      font-size: 14px;
      line-height: 1.6;
    }
    .subtitle { color: #555; margin-bottom: 12px; }
    .loading { display: flex; justify-content: center; padding: 32px; }
    .no-suggestions {
      display: flex; flex-direction: column; align-items: center;
      gap: 8px; padding: 32px; color: #999;
      mat-icon { font-size: 48px; width: 48px; height: 48px; }
    }
    .suggestions-list {
      display: flex; flex-direction: column; gap: 8px;
      max-height: 300px; overflow-y: auto;
    }
    .suggestion-item {
      padding: 12px; border: 2px solid #eee;
      border-radius: 8px; cursor: pointer;
      transition: border-color 0.2s, background 0.2s;
      &:hover { border-color: #667eea; background: #f0f4ff; }
      &.selected { border-color: #667eea; background: #f0f4ff; }
    }
    .suggestion-info {
      display: flex; flex-direction: column; margin-left: 8px;
      .name { font-weight: 600; }
      .details { font-size: 13px; color: #666; }
    }
  `]
})
export class AssignmentDialog implements OnInit {
  suggestions: AccommodationSuggestion[] = [];
  selectedId: string | null = null;
  loading = true;
  confirming = false;

  constructor(
    public dialogRef: MatDialogRef<AssignmentDialog>,
    @Inject(MAT_DIALOG_DATA) public data: {
      bookingId: string;
      accommodationName: string;
      startDate: string;
      endDate: string;
      adults: number;
      children: number;
    },
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.http.get<AccommodationSuggestion[]>(
      `${environment.apiUrl}/api/bookings/${this.data.bookingId}/assignment/suggestions`
    ).subscribe({
      next: data => {
        this.suggestions = data;
        if (data.length > 0) this.selectedId = data[0].id;
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
    if (!this.selectedId) return;
    this.confirming = true;

    this.http.patch(
      `${environment.apiUrl}/api/bookings/${this.data.bookingId}/assignment/assign?accommodationId=${this.selectedId}`,
      {}
    ).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => {
        this.confirming = false;
        this.cdr.detectChanges();
      }
    });
  }
}