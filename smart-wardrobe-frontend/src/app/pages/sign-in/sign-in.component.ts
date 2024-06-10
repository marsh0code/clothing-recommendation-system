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
import { take, tap } from 'rxjs';

interface SignInForm {
  username: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButton
  ],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SignInComponent {
  signInForm: FormGroup<SignInForm> = this.formBuilder.group<SignInForm>({
    username: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
    password: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
  });

  constructor(
    private formBuilder: NonNullableFormBuilder,
    private router: Router,
    private authService: AuthService,
  ) { }

  onSignIn() {
    this.authService.signIn(this.signInForm.getRawValue())
      .pipe(
        take(1),
        tap(() => this.router.navigate(['wardrobe']))
      )
      .subscribe()
  }

  openSignUpPage() {
    this.router.navigate(['sign-up']);
  }
}
