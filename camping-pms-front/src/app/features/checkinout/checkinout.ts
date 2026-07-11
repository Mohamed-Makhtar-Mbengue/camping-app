import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatBadgeModule } from '@angular/material/badge';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Navbar } from '../../shared/components/navbar/navbar';
import { environment } from '../../environments/environment';

interface BookingInfo {
  id: string;
  accommodation: { name: string; type: string; category: string; };
  customer: { firstName: string; lastName: string; email: string; phone: string; };
  startDate: string;
  endDate: string;
  adults: number;
  children: number;
  totalPrice: number;
  status: string;
  depositStatus: string;
  depositAmount: number;
}

interface DayData {
  date: string;
  checkIns: BookingInfo[];
  checkOuts: BookingInfo[];
  currentlyPresent: BookingInfo[];
  totalCheckIns: number;
  totalCheckOuts: number;
  totalPresent: number;
}

@Component({
  selector: 'app-checkinout',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatInputModule,
    MatBadgeModule,
    Navbar
  ],
  templateUrl: './checkinout.html',
  styleUrl: './checkinout.scss'
})
export class Checkinout implements OnInit {
  data: DayData | null = null;
  loading = true;
  selectedDate = new Date();

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadToday();
  }

  loadToday(): void {
    this.loading = true;
    this.http.get<DayData>(`${environment.apiUrl}/api/checkinout/today`).subscribe({
      next: data => {
        this.data = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadByDate(date: Date): void {
    this.loading = true;
    const formatted = this.formatDate(date);
    this.http.get<DayData>(`${environment.apiUrl}/api/checkinout/date/${formatted}`).subscribe({
      next: data => {
        this.data = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onDateChange(date: Date): void {
    this.selectedDate = date;
    this.loadByDate(date);
  }

  isToday(): boolean {
    const today = new Date();
    return this.selectedDate.toDateString() === today.toDateString();
  }

  getNights(startDate: string, endDate: string): number {
    const start = new Date(startDate);
    const end = new Date(endDate);
    return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
  }

  private formatDate(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}