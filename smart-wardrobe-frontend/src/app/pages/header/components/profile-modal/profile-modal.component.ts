import { Component, Inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatSliderModule } from '@angular/material/slider';
import { FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UsersService } from '../../../../shared/users/services';
import { filter, finalize, switchMap, take, tap } from 'rxjs';
import { User } from '../../../../shared/auth/models';
import { AuthService } from '../../../../shared/auth/services';
import { Router } from '@angular/router';

interface ProfileForm {
  id: FormControl<number>;
  image: FormControl<File | null>;
  username: FormControl<string>;
  email: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-profile-modal',
  standalone: true,
    imports: [
      MatButtonModule,
      MatDialogModule,
      MatFormFieldModule,
      MatOptionModule,
      MatSelectModule,
      MatSliderModule,
      ReactiveFormsModule
    ],
  templateUrl: './profile-modal.component.html',
  styleUrl: './profile-modal.component.scss'
})
export class ProfileModalComponent {
  loading = signal<boolean>(false);

  profileForm: FormGroup<ProfileForm> = this.formBuilder.group<ProfileForm>({
    id: this.formBuilder.control<number>(this.data.id),
    image: this.formBuilder.control<File | null>(null),
    username: this.formBuilder.control<string>(this.data.username, [Validators.required, Validators.minLength(4), Validators.maxLength(32)]),
    email: this.formBuilder.control<string>(this.data.email, [Validators.required, Validators.email]),
    password: this.formBuilder.control<string>('MOCK_PASSWORD', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
  });

  constructor(
    private formBuilder: NonNullableFormBuilder,
    private usersService: UsersService,
    @Inject(MAT_DIALOG_DATA) private data: User,
    private dialogRef: MatDialogRef<ProfileModalComponent>,
    private authService: AuthService,
    private router: Router,
  ) { }

  onFileSelected(event: Event) {
    this.profileForm.controls.image.setValue((event.target as HTMLInputElement).files![0]);
  }

  submitForm() {
    this.loading.set(true);
    const { password, ...payloadWithNoPassword } = this.profileForm.getRawValue();
    const payload = this.profileForm.controls.password.dirty ? { password, ...payloadWithNoPassword } : payloadWithNoPassword;

    this.usersService.editUser(payload)
      .pipe(
        take(1),
        finalize(() => {
          if (!this.profileForm.value.image) {
            this.dialogRef.close();
            this.usersService.loadClothesCollection();
            this.loading.set(false);
            this.authService.logOut();
            this.router.navigate(['sign-in'])
          }
        }),
        filter(() => !!this.profileForm.value.image),
        switchMap(() => this.usersService.saveUserPhoto(this.profileForm.value.image!)),
        finalize(() => {
          this.dialogRef.close();
          this.loading.set(false);
          this.authService.logOut();
          this.router.navigate(['sign-in'])
        }),
      )
      .subscribe()
  }
}
