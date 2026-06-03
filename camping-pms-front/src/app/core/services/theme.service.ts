import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  isDarkMode = signal<boolean>(false);

  constructor() {
    const saved = localStorage.getItem('darkMode');
    if (saved === 'true') {
      this.isDarkMode.set(true);
      document.body.classList.add('dark-mode');
    }
  }

  toggleTheme(): void {
    const newValue = !this.isDarkMode();
    this.isDarkMode.set(newValue);
    localStorage.setItem('darkMode', String(newValue));
    if (newValue) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }
}