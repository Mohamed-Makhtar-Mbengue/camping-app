import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { PublicNavbar } from '../../../shared/components/public-navbar/public-navbar';
import {
  PublicService,
  BookingConfirmation
} from '../../../core/services/public.service';

@Component({
  selector: 'app-confirmation',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    PublicNavbar
  ],
  templateUrl: './confirmation.html',
  styleUrl: './confirmation.scss'
})
export class Confirmation implements OnInit {

  confirmation: BookingConfirmation | null = null;
  downloading = false;

  constructor(
    private router: Router,
    private publicService: PublicService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();

    if (nav?.extras?.state?.['confirmation']) {
      this.confirmation = nav.extras.state['confirmation'];
    } else if (history.state?.confirmation) {
      this.confirmation = history.state.confirmation;
    }

    this.cdr.detectChanges();
  }

  getNights(): number {
    if (!this.confirmation) {
      return 0;
    }

    const start = new Date(this.confirmation.startDate);
    const end = new Date(this.confirmation.endDate);

    return Math.ceil(
      (end.getTime() - start.getTime()) /
      (1000 * 60 * 60 * 24)
    );
  }

  downloadPdf(): void {
    if (!this.confirmation) {
      return;
    }

    this.downloading = true;

    this.publicService
      .downloadBonEchange(this.confirmation.bookingId)
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);

          const a = document.createElement('a');
          a.href = url;
          a.download = `bon-echange-${this.confirmation!.bookingId}.pdf`;
          a.click();

          URL.revokeObjectURL(url);

          this.downloading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.downloading = false;
          this.cdr.detectChanges();
        }
      });
  }
}