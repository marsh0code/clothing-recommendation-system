import { ChangeDetectionStrategy, Component, computed, OnInit, signal } from '@angular/core';

import { WardrobeItemComponent, WeatherWidgetComponent, WardrobeItemModalComponent } from './components';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { UsersService } from '../../shared/users/services';
import { FormsModule } from '@angular/forms';
import { WardrobeService, WeatherService } from './services';
import { take, tap } from 'rxjs';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { ClothingItemResponse, ClothingType, RecommendedClothingItemResponse } from '../../shared/models';
import { KeyValuePipe } from '@angular/common';
import { LocationNotAllowedComponent } from '../../shared/location-not-allowed/location-not-allowed.component';

@Component({
  selector: 'app-wardrobe',
  standalone: true,
  imports: [WardrobeItemComponent, WeatherWidgetComponent, MatButtonModule, MatButtonModule, MatDialogModule, FormsModule, MatSlideToggle, KeyValuePipe, LocationNotAllowedComponent],
  templateUrl: './wardrobe-page.component.html',
  styleUrl: 'wardrobe-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WardrobePageComponent implements OnInit {
  clothes$ = this.usersService.clothes$;
  recommendedClothes$ = this.usersService.recommendedClothes$;
  searchValue$ = signal<string>('');
  clothesFilteredByName$ = computed(() => this.clothes$().filter((item) => item.name.toLowerCase().includes(this.searchValue$().toLowerCase())));
  recommendedClothesGroupedByType$ = computed(() => {
    return this.recommendedClothes$()
        .filter((item) => item.clothingItem.name.toLowerCase().includes(this.searchValue$().toLowerCase()))
        .reduce((acc: Record<ClothingType, RecommendedClothingItemResponse[]>, item: RecommendedClothingItemResponse) => {
          acc[item.clothingItem.clothingType] ? acc[item.clothingItem.clothingType].push(item) : acc[item.clothingItem.clothingType] = [item];
          return acc;
        }, { } as Record<ClothingType, RecommendedClothingItemResponse[]>);
  });
  recommendationMode$ = signal<boolean>(false);
  doesLocationAllowed$ = this.weatherService.doesLocationAllowed$;

  readonly clothTypeLabel: Record<string, string> = {
    [ClothingType.BottomWear]: 'Нижній одяг',
    [ClothingType.FootWear]: 'Взуття',
    [ClothingType.HeadWear]: 'Головний убір',
    [ClothingType.OuterWear]: 'Зовнішній одяг',
    [ClothingType.TopWear]: 'Верхній одяг',
  }

  constructor(
    private dialog: MatDialog,
    private usersService: UsersService,
    private wardrobeService: WardrobeService,
    private weatherService: WeatherService,
  ) {
  }

  ngOnInit() {
    this.usersService.loadClothesCollection();
  }

  openItemCreationModal() {
    this.dialog.closeAll();
    this.dialog.open(WardrobeItemModalComponent);
  }

  openItemEditionModal(id: string) {
    this.dialog.closeAll();
    this.dialog.open(WardrobeItemModalComponent, { data: this.clothes$().find((item) => item.id === id) });
  }

  deleteItem(id: string) {
    this.wardrobeService.deleteClothingItem(id)
      .pipe(
        take(1),
        tap(() => this.usersService.loadClothesCollection()),
      )
      .subscribe()
  }
}
