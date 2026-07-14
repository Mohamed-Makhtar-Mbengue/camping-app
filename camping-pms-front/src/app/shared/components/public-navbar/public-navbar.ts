import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { ThemeService } from '../../../core/services/theme.service';
import { LanguageService } from '../../../core/services/language.service';

@Component({
  selector: 'app-public-navbar',
  imports: [
    CommonModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    TranslatePipe
  ],
  templateUrl: './public-navbar.html',
  styleUrl: './public-navbar.scss'
})
export class PublicNavbar implements OnInit {
  constructor(
    public themeService: ThemeService,
    public langService: LanguageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cdr.detectChanges();
  }

  setLanguage(lang: string): void {
    this.langService.setLanguage(lang);
    this.cdr.markForCheck();
    setTimeout(() => this.cdr.detectChanges(), 100);
  }
}