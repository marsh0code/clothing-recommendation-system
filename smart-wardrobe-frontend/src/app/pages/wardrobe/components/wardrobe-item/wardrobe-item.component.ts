import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { ClothingItemResponse } from '../../../../shared/models';
import { GetSecuredImagePipe } from '../../../../shared/tools/pipes/get-secured-image.pipe';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'app-wardrobe-item',
  standalone: true,
  templateUrl: './wardrobe-item.component.html',
  styleUrl: './wardrobe-item.component.scss',
  imports: [
    CommonModule,
    GetSecuredImagePipe,
    MatButtonModule,
    FaIconComponent,
    MatIcon,
    MatTooltip,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WardrobeItemComponent {
  @Input({ required: true }) item!: ClothingItemResponse;
  @Input() suppressActions = false;
  @Input() percentage?: number;

  @Output() itemDeleted = new EventEmitter<string>();
  @Output() itemEdited = new EventEmitter<string>();
}
