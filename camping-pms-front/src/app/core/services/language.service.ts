import { Injectable, signal } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

export interface Language {
  code: string;
  label: string;
  flag: string;
}

@Injectable({ providedIn: 'root' })
export class LanguageService {

  currentLang = signal<string>('fr');

  languages: Language[] = [
    { code: 'fr', label: 'Français', flag: '🇫🇷' },
    { code: 'en', label: 'English', flag: '🇬🇧' },
    { code: 'de', label: 'Deutsch', flag: '🇩🇪' },
    { code: 'nl', label: 'Nederlands', flag: '🇳🇱' }
  ];

  constructor(private translate: TranslateService) {
    const saved = localStorage.getItem('lang') || 'fr';
    this.setLanguage(saved);
  }

  setLanguage(lang: string): void {
    this.translate.use(lang);
    this.currentLang.set(lang);
    localStorage.setItem('lang', lang);
    document.documentElement.lang = lang;
  }

  getCurrentLanguage(): Language {
    return this.languages.find(l => l.code === this.currentLang()) || this.languages[0];
  }
}