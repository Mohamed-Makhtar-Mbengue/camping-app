import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Navbar } from '../../shared/components/navbar/navbar';

interface Stats {
  totalAccommodations: number;
  totalBookings: number;
  pendingBookings: number;
  confirmedBookings: number;
  cancelledBookings: number;
  totalRevenue: number;
  totalPersons: number;
  occupancyRate: number;
  monthRevenue: number;
  revenueByMonth: { month: string; bookings: number; revenue: number; }[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    RouterLink,
    Navbar
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  stats: Stats | null = null;
  loading = true;
  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1;
  monthNames = [
    'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
    'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
  ];

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;
    this.http.get<Stats>(`${environment.apiUrl}/api/stats`).subscribe({
      next: data => {
        this.stats = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getOccupancyColor(): string {
    if (!this.stats) return '#667eea';
    if (this.stats.occupancyRate >= 80) return '#f44336';
    if (this.stats.occupancyRate >= 50) return '#ff9800';
    return '#4caf50';
  }

  getMaxRevenue(): number {
    if (!this.stats?.revenueByMonth?.length) return 1;
    return Math.max(...this.stats.revenueByMonth.map(r => Number(r['revenue'])));
  }

  getBarHeight(revenue: number): number {
    const max = this.getMaxRevenue();
    return max > 0 ? (revenue / max) * 100 : 0;
  }

  formatMonth(monthStr: string): string {
    const [year, month] = monthStr.split('-');
    return this.monthNames[parseInt(month) - 1].substring(0, 3);
  }
}