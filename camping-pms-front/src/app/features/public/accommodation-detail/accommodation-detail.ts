import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PublicNavbar } from '../../../shared/components/public-navbar/public-navbar';
import { PublicService } from '../../../core/services/public.service';
import { Accommodation } from '../../../core/services/accommodation.service';

@Component({
  selector: 'app-accommodation-detail',
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    PublicNavbar
  ],
  templateUrl: './accommodation-detail.html',
  styleUrl: './accommodation-detail.scss'
})
export class AccommodationDetail implements OnInit {
  accommodation: Accommodation | null = null;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private publicService: PublicService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.publicService.getAccommodation(id).subscribe({
      next: data => {
        this.accommodation = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getPlaceholderImage(type: string): string {
    const images: Record<string, string> = {
      'TENTE': 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=800&h=400&fit=crop',
      'MOBIL_HOME': 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=400&fit=crop',
      'CHALET': 'https://images.unsplash.com/photo-1518732714860-b62714ce0c59?w=800&h=400&fit=crop',
      'BUNGALOW': 'https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=800&h=400&fit=crop'
    };
    return images[type] || 'https://images.unsplash.com/photo-1537225228614-56cc3556d7ed?w=800&h=400&fit=crop';
  }
}