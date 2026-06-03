import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { BookingService, Booking } from '../../../core/services/booking';
import { AuthService } from '../../../core/services/auth.service';
import { BookingForm } from '../booking-form/booking-form';

@Component({
  selector: 'app-booking-list',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    Navbar
  ],
  templateUrl: './booking-list.html',
  styleUrl: './booking-list.scss'
})
export class BookingList implements OnInit {
  bookings: Booking[] = [];
  loading = true;

  constructor(
    private bookingService: BookingService,
    public authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    const obs = this.authService.isAdmin()
      ? this.bookingService.getAll()
      : this.bookingService.getMine();

    obs.subscribe({
      next: (data: Booking[]) => {
        this.bookings = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openForm(): void {
    const dialogRef = this.dialog.open(BookingForm, { width: '600px' });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadBookings();
    });
  }

  updateStatus(id: string, status: string): void {
    this.bookingService.updateStatus(id, status).subscribe({
      next: () => {
        this.snackBar.open('Statut mis à jour', 'Fermer', { duration: 3000 });
        this.loadBookings();
      }
    });
  }

  delete(id: string): void {
    if (confirm('Annuler cette réservation ?')) {
      this.bookingService.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Réservation supprimée', 'Fermer', { duration: 3000 });
          this.loadBookings();
        }
      });
    }
  }

  getStatusColor(status: string): string {
    const colors: Record<string, string> = {
      'PENDING': 'accent',
      'CONFIRMED': 'primary',
      'CANCELLED': 'warn'
    };
    return colors[status] || 'accent';
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      'PENDING': '⏳ En attente',
      'CONFIRMED': '✅ Confirmée',
      'CANCELLED': '❌ Annulée'
    };
    return labels[status] || status;
  }

  getNights(startDate: string, endDate: string): number {
    const start = new Date(startDate);
    const end = new Date(endDate);
    return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  }
}