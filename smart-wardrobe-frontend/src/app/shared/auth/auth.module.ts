import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

import { AuthenticatedUserGuard } from './guards';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
  ],
  providers: [
    AuthenticatedUserGuard,
  ],
})
export class AuthModule {
}
