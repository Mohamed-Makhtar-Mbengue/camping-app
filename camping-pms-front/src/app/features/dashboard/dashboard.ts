import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Navbar } from '../../shared/components/navbar/navbar';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, MatCardModule, MatIconModule, RouterLink, Navbar],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  stats = {
    accommodations: 0,
    bookings: 0,
    myBookings: 0
  };

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.http.get<any>(`${environment.apiUrl}/api/accommodations?page=0&size=1`)
      .subscribe(data => {
        this.stats.accommodations = data.totalElements;
        this.cdr.detectChanges();
      });

    this.http.get<any[]>(`${environment.apiUrl}/api/bookings/my`)
      .subscribe(data => {
        this.stats.myBookings = data.length;
        this.cdr.detectChanges();
      });
  }
}