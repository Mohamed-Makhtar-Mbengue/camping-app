import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { PublicNavbar } from '../../../shared/components/public-navbar/public-navbar';
import { BookingConfirmation } from '../../../core/services/public.service';

@Component({
  selector: 'app-confirmation',
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    PublicNavbar
  ],
  templateUrl: './confirmation.html',
  styleUrl: './confirmation.scss'
})
export class Confirmation implements OnInit {
  confirmation: BookingConfirmation | null = null;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    if (nav?.extras?.state?.['confirmation']) {
      this.confirmation = nav.extras.state['confirmation'];
    } else {
      const state = history.state;
      if (state?.confirmation) {
        this.confirmation = state.confirmation;
      }
    }
    this.cdr.detectChanges();
  }

  getNights(): number {
    if (!this.confirmation) return 0;
    const start = new Date(this.confirmation.startDate);
    const end = new Date(this.confirmation.endDate);
    return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  }
}