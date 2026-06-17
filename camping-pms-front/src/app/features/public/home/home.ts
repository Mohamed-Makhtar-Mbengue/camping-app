import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
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
export class Home implements OnInit, OnDestroy {
  accommodations: Accommodation[] = [];
  totalElements = 0;
  pageSize = 9;
  currentPage = 0;
  loading = true;

  heroImages = [
    { url: 'https://images.unsplash.com/photo-1519046904884-53103b34b206?w=1600&h=900&fit=crop', label: 'Piscine' },
    { url: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1600&h=900&fit=crop', label: 'Restaurant' },
    { url: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=1600&h=900&fit=crop', label: 'Espace bien-être' },
    { url: 'https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=1600&h=900&fit=crop', label: 'Terrain multisport' },
    { url: 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=1600&h=900&fit=crop', label: 'Salle de sport' },
    { url: 'https://images.unsplash.com/photo-1560253787-29c50689bbe5?w=1600&h=900&fit=crop', label: 'Espace jeux gonflables' }
  ];
  currentHeroIndex = 0;
  private heroInterval: any;

  constructor(
    private publicService: PublicService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAccommodations();
    this.startHeroCarousel();
  }

  ngOnDestroy(): void {
    if (this.heroInterval) clearInterval(this.heroInterval);
  }

  startHeroCarousel(): void {
    this.heroInterval = setInterval(() => {
      this.currentHeroIndex = (this.currentHeroIndex + 1) % this.heroImages.length;
      this.cdr.detectChanges();
    }, 4000);
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
    document.getElementById('accommodations')?.scrollIntoView({ behavior: 'smooth' });
  }

  getTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      'TENTE': '⛺', 'MOBIL_HOME': '🏠', 'CHALET': '🏔️',
      'BUNGALOW': '🏡', 'TIPI': '🛖', 'CABANE': '🌳', 'EMPLACEMENT': '🚐'
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