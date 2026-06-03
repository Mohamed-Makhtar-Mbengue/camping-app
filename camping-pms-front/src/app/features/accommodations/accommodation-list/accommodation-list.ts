import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AccommodationService, Accommodation } from '../../../core/services/accommodation.service';
import { AuthService } from '../../../core/services/auth.service';
import { AccommodationForm } from '../accommodation-form/accommodation-form';

@Component({
  selector: 'app-accommodation-list',
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    Navbar
  ],
  templateUrl: './accommodation-list.html',
  styleUrl: './accommodation-list.scss'
})
export class AccommodationList implements OnInit {
  accommodations: Accommodation[] = [];
  totalElements = 0;
  pageSize = 9;
  currentPage = 0;
  loading = true;

  constructor(
    private accommodationService: AccommodationService,
    public authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccommodations();
  }

  loadAccommodations(): void {
    this.loading = true;
    this.accommodationService.getAll(this.currentPage, this.pageSize).subscribe({
      next: data => {
        this.accommodations = data.content;
        this.totalElements = data.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadAccommodations();
  }

  openForm(accommodation?: Accommodation): void {
    const dialogRef = this.dialog.open(AccommodationForm, {
      width: '500px',
      data: accommodation || null
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadAccommodations();
    });
  }

  delete(id: string): void {
    if (confirm('Supprimer cet hébergement ?')) {
      this.accommodationService.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Hébergement supprimé', 'Fermer', { duration: 3000 });
          this.loadAccommodations();
        }
      });
    }
  }

  getTypeColor(type: string): string {
    const colors: Record<string, string> = {
      'TENTE': 'primary',
      'MOBIL_HOME': 'accent',
      'CHALET': 'warn',
      'BUNGALOW': ''
    };
    return colors[type] || 'primary';
  }

  getTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      'TENTE': '⛺',
      'MOBIL_HOME': '🏠',
      'CHALET': '🏔️',
      'BUNGALOW': '🏡'
    };
    return icons[type] || '🏕️';
  }
}