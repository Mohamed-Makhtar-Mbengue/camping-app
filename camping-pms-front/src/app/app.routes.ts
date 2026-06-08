import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  // Routes publiques
  {
    path: '',
    loadComponent: () => import('./features/public/home/home')
      .then(m => m.Home)
  },
  {
    path: 'hebergements',
    loadComponent: () => import('./features/public/home/home')
      .then(m => m.Home)
  },
  {
    path: 'hebergements/:id',
    loadComponent: () => import('./features/public/accommodation-detail/accommodation-detail')
      .then(m => m.AccommodationDetail)
  },
  {
    path: 'reservation/:id',
    loadComponent: () => import('./features/public/booking-wizard/booking-wizard')
      .then(m => m.BookingWizard)
  },
  {
    path: 'confirmation',
    loadComponent: () => import('./features/public/confirmation/confirmation')
      .then(m => m.Confirmation)
  },

  // Routes privées
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login')
      .then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register')
      .then(m => m.Register)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard')
      .then(m => m.Dashboard)
  },
  {
    path: 'accommodations',
    canActivate: [authGuard],
    loadComponent: () => import('./features/accommodations/accommodation-list/accommodation-list')
      .then(m => m.AccommodationList)
  },
  {
    path: 'bookings',
    canActivate: [authGuard],
    loadComponent: () => import('./features/bookings/booking-list/booking-list')
      .then(m => m.BookingList)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile')
      .then(m => m.Profile)
  },
  { path: '**', redirectTo: '' }
];