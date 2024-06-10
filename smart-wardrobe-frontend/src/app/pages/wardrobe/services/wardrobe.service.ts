import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  ClothingItemBaseDto,
  ClothingItemEditDto,
  ClothingItemResponse
} from '../../../shared/models';

@Injectable({providedIn: 'root'})
export class WardrobeService {
  private basic = 'http://localhost:8080/clothes';

  constructor(private _httpClient: HttpClient) {
  }

  createClothingItem(clothingItemDto: ClothingItemBaseDto) {
    return this._httpClient.post<ClothingItemResponse>(this.basic, clothingItemDto);
  }

  editClothingItem(clothingItemDto: ClothingItemEditDto) {
    return this._httpClient.patch<ClothingItemResponse>(`${this.basic}/${clothingItemDto.id}`, clothingItemDto);
  }

  saveItemPhoto(id: string, image: File) {
    const formData = new FormData();
    formData.append('file', image);

    return this._httpClient.post(`${this.basic}/${id}/image`, formData, {responseType: 'text'});
  }

  deleteClothingItem(id: string) {
    return this._httpClient.delete(`${this.basic}/${id}`);
  }
}
