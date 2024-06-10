import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { AuthService } from '../../shared/auth/services';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { Router, RouterModule } from '@angular/router';
import { UsersService } from '../../shared/users/services';
import { MatDialog } from '@angular/material/dialog';
import { ProfileModalComponent } from './components/profile-modal/profile-modal.component';
import { GetSecuredImagePipe } from '../../shared/tools/pipes/get-secured-image.pipe';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatMenuModule,
    MatToolbarModule,
    MatButtonModule,
    GetSecuredImagePipe,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  isAuthenticated$ = this.authService.isAuthenticated$;
  user$ = this.usersService.user$;

  constructor(
    private usersService: UsersService,
    private authService: AuthService,
    private dialog: MatDialog,
  ) {
  }

  logOut() {
    this.authService.logOut();
  }

  openProfile() {
    this.dialog.open(ProfileModalComponent, { data: this.user$() });
  }
}
