import { ANIMATION_MODULE_TYPE, ChangeDetectionStrategy, Component, Inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSliderModule } from '@angular/material/slider';
import { FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ClothingItemResponse, ClothingType, StyleType } from '../../../../shared/models';
import { WardrobeService } from '../../services';
import { filter, finalize, switchMap, take, tap } from 'rxjs';
import { UsersService } from '../../../../shared/users/services';

interface WardrobeItemForm {
  id: FormControl<string>
  image: FormControl<File | null>
  name: FormControl<string>
  warmth: FormControl<number>
  windResistance: FormControl<number>
  waterResistance: FormControl<number>
  styleCoefficient: FormControl<number>
  clothingType: FormControl<ClothingType>
  styleType: FormControl<StyleType>
}

@Component({
  selector: 'app-wardrobe-item-modal',
  standalone: true,
  imports: [MatDialogModule, MatSliderModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatSelectModule],
  providers: [
    {provide: ANIMATION_MODULE_TYPE, useValue: 'NoopAnimations'},
  ],
  templateUrl: './wardrobe-item-modal.component.html',
  styleUrl: './wardrobe-item-modal.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WardrobeItemModalComponent {
  loading = signal<boolean>(false);

  wardrobeItemForm = this.formBuilder.group<WardrobeItemForm>({
    id: this.formBuilder.control(this.data?.id),
    image: this.formBuilder.control(null),
    name: this.formBuilder.control(this.data?.name ?? '', [Validators.required, Validators.minLength(4), Validators.maxLength(64)]),
    styleCoefficient: this.formBuilder.control(this.data?.styleCoefficient ?? 5, [Validators.min(0), Validators.max(10)]),
    waterResistance: this.formBuilder.control(this.data?.waterResistance ?? 5, [Validators.min(0), Validators.max(10)]),
    windResistance: this.formBuilder.control(this.data?.windResistance ?? 5, [Validators.min(0), Validators.max(10)]),
    warmth: this.formBuilder.control(this.data?.warmth ?? 5, [Validators.min(0), Validators.max(10)]),
    styleType: this.formBuilder.control(this.data?.styleType ?? StyleType.Casual, [Validators.required]),
    clothingType: this.formBuilder.control(this.data?.clothingType ?? ClothingType.OuterWear, [Validators.required]),
  })

  readonly ClothingType = ClothingType;
  readonly StyleType = StyleType;

  constructor(
    private formBuilder: NonNullableFormBuilder,
    private wardrobeService: WardrobeService,
    private usersService: UsersService,
    @Inject(MAT_DIALOG_DATA) private data: ClothingItemResponse,
    private dialogRef: MatDialogRef<WardrobeItemModalComponent>,
  ) {
  }

  submitForm() {
    this.loading.set(true);

    (this.data?.id ? this.wardrobeService.editClothingItem(this.wardrobeItemForm.getRawValue()) : this.wardrobeService.createClothingItem(this.wardrobeItemForm.getRawValue()))
      .pipe(
        take(1),
        finalize(() => {
          if (!this.wardrobeItemForm.value.image) {
            this.dialogRef.close();
            this.usersService.loadClothesCollection();
            this.loading.set(false);
          }
        }),
        filter(() => !!this.wardrobeItemForm.value.image),
        switchMap((response) => this.wardrobeService.saveItemPhoto(response.id, this.wardrobeItemForm.value.image!)),
        finalize(() => {
          this.dialogRef.close();
          this.usersService.loadClothesCollection();
          this.loading.set(false);
        }),
      )
      .subscribe()
  }

  onFileSelected(event: Event) {
    this.wardrobeItemForm.controls.image.setValue((event.target as HTMLInputElement).files![0]);
  }
}
