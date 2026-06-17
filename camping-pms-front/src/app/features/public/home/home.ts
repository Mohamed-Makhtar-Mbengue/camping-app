import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PublicNavbar } from '../../../shared/components/public-navbar/public-navbar';
import { PublicService } from '../../../core/services/public.service';
import { Accommodation } from '../../../core/services/accommodation.service';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatChipsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    PublicNavbar
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home implements OnInit {
  accommodations: Accommodation[] = [];
  totalElements = 0;
  pageSize = 9;
  currentPage = 0;
  loading = true;
  showAccommodations = false;

  constructor(
    private publicService: PublicService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccommodations();
  }

  loadAccommodations(): void {
    this.loading = true;
    this.publicService.getAccommodations(this.currentPage, this.pageSize).subscribe({
      next: data => {
        this.accommodations = data.content;
        this.totalElements = data.totalElements;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
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

  scrollToAccommodations(): void {
    this.showAccommodations = true;
    setTimeout(() => {
      document.getElementById('accommodations')?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
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

  getPlaceholderImage(type: string): string {
    const images: Record<string, string> = {
      'MOBIL_HOME': 'https://picsum.photos/seed/mobilhome/400/250',
      'CHALET': 'https://picsum.photos/seed/chalet/400/250',
      'TENTE': 'https://picsum.photos/seed/tente/400/250',
      'TIPI': 'https://picsum.photos/seed/tipi/400/250',
      'CABANE': 'https://picsum.photos/seed/cabane/400/250',
      'EMPLACEMENT': 'https://picsum.photos/seed/emplacement/400/250'
    };
    return images[type] || 'https://picsum.photos/seed/camping/400/250';
  }
}