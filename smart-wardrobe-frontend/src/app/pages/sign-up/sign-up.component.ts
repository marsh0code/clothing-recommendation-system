import { ChangeDetectionStrategy, Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';
import { AuthService } from '../../shared/auth/services';
import { switchMap, take, tap } from 'rxjs';

interface SignUpForm {
  username: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButton
  ],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignUpComponent {
  signUpForm: FormGroup<SignUpForm> = this.formBuilder.group<SignUpForm>({
    username: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
    email: this.formBuilder.control<string>('', [Validators.required, Validators.email]),
    password: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
  });

  constructor(
    private formBuilder: NonNullableFormBuilder,
    private authService: AuthService,
    private router: Router,
  ) { }

  onSignUp() {
    this.authService.signUp(this.signUpForm.getRawValue())
      .pipe(
        take(1),
        switchMap(() => this.authService.signIn(this.signUpForm.getRawValue())),
        tap(() => this.router.navigate(['wardrobe'])),
      )
      .subscribe()
  }

  openSignInPage() {
    this.router.navigate(['sign-in']);
  }
}
