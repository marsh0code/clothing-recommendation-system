import { computed, effect, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { jwtDecode } from 'jwt-decode';
import { Observable, tap } from 'rxjs';

import { User, UserCredentials } from '../models';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  userId$ = signal<number | null>(null);
  isAuthenticated$ = computed(() => !!this.userId$());

  private basic = 'http://localhost:8080/auth';

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
  }

  isTokenValid(token: string): boolean {
    try {
      const decodedToken: { id: number, sub: string, exp: number } = jwtDecode(token);
      const currentTime = Math.floor(Date.now() / 1000);

      if (decodedToken.exp < currentTime) {
        return false;
      }

      this.userId$.set(decodedToken.id);
      return true;
    } catch (error) {
      return false;
    }
  }

  signIn({ username, password }: { username: string, password: string }): Observable<UserCredentials> {
    return this.http.post<UserCredentials>(this.basic + '/sign-in', {username, password})
      .pipe(
        tap(({ token }) => localStorage.setItem('jwtToken', token))
      );
  }

  signUp({ email, username, password }: { email: string, username: string, password: string }): Observable<UserCredentials> {
    return this.http.post<UserCredentials>(this.basic + '/sign-up', {email, username, password});
  }

  logOut() {
    this.userId$.set(null);
    localStorage.removeItem('jwtToken');
    this.router.navigate(['sign-in']);
  }
}
