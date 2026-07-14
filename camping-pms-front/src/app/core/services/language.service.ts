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
    // Applique la langue sans recharger
    this.translate.use(saved);
    this.currentLang.set(saved);
    document.documentElement.lang = saved;
  }

  setLanguage(lang: string): void {
    if (lang === this.currentLang()) return; // évite boucle si même langue
    localStorage.setItem('lang', lang);
    // Recharge uniquement si langue différente
    window.location.href = window.location.href.split('?')[0] + '?lang=' + lang;
  }

  getCurrentLanguage(): Language {
    return this.languages.find(l => l.code === this.currentLang()) || this.languages[0];
  }
}