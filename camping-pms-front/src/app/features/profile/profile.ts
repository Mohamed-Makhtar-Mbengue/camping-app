import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Navbar } from '../../shared/components/navbar/navbar';
import { AuthService, Customer } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule, Navbar],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {
  user: Customer | null = null;

  constructor(
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
      this.cdr.detectChanges();
    });
  }
}