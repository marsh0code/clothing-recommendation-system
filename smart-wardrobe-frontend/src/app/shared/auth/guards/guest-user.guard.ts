import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from '../services';

@Injectable()
export class GuestUserGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(): boolean {
    const token = localStorage.getItem('jwtToken');

    if (token) {
      const isTokenValid = this.authService.isTokenValid(token);

      if (isTokenValid) {
        this.router.navigate(['wardrobe']);
        return false;
      }
    }

    return true;
  }
}
