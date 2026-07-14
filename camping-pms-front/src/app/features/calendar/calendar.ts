import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Navbar } from '../../shared/components/navbar/navbar';
import { environment } from '../../environments/environment';

interface BookingDto {
  id: string;
  accommodation: { id: string; name: string; type: string; category: string; };
  customer: { firstName: string; lastName: string; };
  startDate: string;
  endDate: string;
  adults: number;
  children: number;
  totalPrice: number;
  status: string;
}

interface CalendarDay {
  date: Date;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  bookings: BookingDto[];
  checkIns: BookingDto[];
  checkOuts: BookingDto[];
  occupied: number;
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatSelectModule,
    MatFormFieldModule,
    Navbar
  ],
  templateUrl: './calendar.html',
  styleUrl: './calendar.scss'
})
export class Calendar implements OnInit {
  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth() + 1;
  loading = true;
  bookings: BookingDto[] = [];
  calendarDays: CalendarDay[] = [];
  selectedDay: CalendarDay | null = null;

  monthNames = [
    'Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin',
    'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'
  ];

  dayNames = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadMonth();
  }

  goToToday(): void {
    const today = new Date();
    this.currentYear = today.getFullYear();
    this.currentMonth = today.getMonth() + 1;
    this.loadMonth();
  }

  loadMonth(): void {
    this.loading = true;
    this.selectedDay = null;
    this.http.get<any>(
      `${environment.apiUrl}/api/calendar/month/${this.currentYear}/${this.currentMonth}`
    ).subscribe({
      next: data => {
        this.bookings = data.bookings;
        this.buildCalendar();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  buildCalendar(): void {
    const days: CalendarDay[] = [];
    const firstDay = new Date(this.currentYear, this.currentMonth - 1, 1);
    const lastDay = new Date(this.currentYear, this.currentMonth, 0);
    const today = new Date();

    // Jour de la semaine du 1er (0=Dim → on veut 0=Lun)
    let startDow = firstDay.getDay();
    startDow = startDow === 0 ? 6 : startDow - 1;

    // Jours du mois précédent
    for (let i = startDow - 1; i >= 0; i--) {
      const date = new Date(firstDay);
      date.setDate(date.getDate() - i - 1);
      days.push(this.createDay(date, false, today));
    }

    // Jours du mois courant
    for (let d = 1; d <= lastDay.getDate(); d++) {
      const date = new Date(this.currentYear, this.currentMonth - 1, d);
      days.push(this.createDay(date, true, today));
    }

    // Compléter jusqu'à 42 cases (6 semaines)
    while (days.length < 42) {
      const date = new Date(days[days.length - 1].date);
      date.setDate(date.getDate() + 1);
      days.push(this.createDay(date, false, today));
    }

    this.calendarDays = days;
  }

  createDay(date: Date, isCurrentMonth: boolean, today: Date): CalendarDay {
    const dateStr = this.formatDate(date);

    const dayBookings = this.bookings.filter(b => {
      const start = new Date(b.startDate);
      const end = new Date(b.endDate);
      return date >= start && date < end;
    });

    const checkIns = this.bookings.filter(b =>
      new Date(b.startDate).toDateString() === date.toDateString()
    );

    const checkOuts = this.bookings.filter(b =>
      new Date(b.endDate).toDateString() === date.toDateString()
    );

    return {
      date,
      dayNumber: date.getDate(),
      isCurrentMonth,
      isToday: date.toDateString() === today.toDateString(),
      bookings: dayBookings,
      checkIns,
      checkOuts,
      occupied: dayBookings.length
    };
  }

  prevMonth(): void {
    if (this.currentMonth === 1) {
      this.currentMonth = 12;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }
    this.loadMonth();
  }

  nextMonth(): void {
    if (this.currentMonth === 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }
    this.loadMonth();
  }

  selectDay(day: CalendarDay): void {
    if (!day.isCurrentMonth) return;
    this.selectedDay = day;
    this.cdr.detectChanges();
  }

  getOccupancyColor(occupied: number): string {
    if (occupied === 0) return '';
    if (occupied <= 5) return 'low';
    if (occupied <= 15) return 'medium';
    return 'high';
  }

  getTotalPersons(): number {
    if (!this.selectedDay) return 0;
    return this.selectedDay.bookings.reduce((sum, b) =>
      sum + b.adults + (b.children || 0), 0);
  }

  private formatDate(date: Date): string {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}