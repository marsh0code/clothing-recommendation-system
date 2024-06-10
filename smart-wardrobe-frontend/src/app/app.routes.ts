import { Routes } from '@angular/router';
import { AuthenticatedUserGuard, GuestUserGuard } from './shared/auth/guards';

export const routes: Routes = [
  {
    path: 'sign-in',
    loadComponent: () => import('./pages/sign-in/sign-in.component').then(m => m.SignInComponent),
    canActivate: [GuestUserGuard],
  },
  {
    path: 'sign-up',
    loadComponent: () => import('./pages/sign-up/sign-up.component').then(m => m.SignUpComponent),
    canActivate: [GuestUserGuard],
  },
  {
    path: 'wardrobe',
    loadComponent: () => import('./pages/wardrobe/wardrobe-page.component').then(m => m.WardrobePageComponent),
    canActivate: [AuthenticatedUserGuard],
  },
  { path: '**', redirectTo: 'wardrobe' },
];
