import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  // Site public
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

  // Auth client
  {
    path: 'mon-espace/login',
    loadComponent: () => import('./features/client/login/client-login')
      .then(m => m.ClientLogin)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register')
      .then(m => m.Register)
  },
  {
    path: 'mon-espace',
    canActivate: [authGuard],
    loadComponent: () => import('./features/client/dashboard/client-dashboard')
      .then(m => m.ClientDashboard)
  },

  // Auth staff (discret)
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login')
      .then(m => m.Login)
  },

  // Admin/Staff
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
  {
  path: 'checkinout',
  canActivate: [authGuard],
  loadComponent: () => import('./features/checkinout/checkinout')
    .then(m => m.Checkinout)
  },
  { path: '**', redirectTo: '' }
];