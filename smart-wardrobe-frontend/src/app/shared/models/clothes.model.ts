export interface ClothingItemBaseDto {
  name: string;
  warmth: number;
  windResistance: number;
  waterResistance: number;
  styleCoefficient: number;
  clothingType: ClothingType;
  styleType: StyleType;
}

export interface ClothingItemEditDto extends ClothingItemBaseDto {
  id: string;
}

export interface ClothingItemResponse extends ClothingItemBaseDto {
  imageUrl: string;
  id: string;
}

export interface RecommendedClothingItemResponse {
  percentage: number;
  clothingItem: ClothingItemResponse;
}

export interface ClothingItem {
  name: string;
  warmth: number;
  windResistance: number;
  waterResistance: number;
  styleCoefficient: number;
  clothingType: ClothingType;
  styleType: StyleType;
}

export enum ClothingType {
  HeadWear = 'HEAD_WEAR',
  OuterWear = 'OUTER_WEAR',
  TopWear = 'TOP_WEAR',
  BottomWear = 'BOTTOM_WEAR',
  FootWear = 'FOOTWEAR'
}

export enum StyleType {
  Home = 'HOME',
  Casual = 'CASUAL',
  Official = 'OFFICIAL',
  Sport = 'SPORT',
  Other = 'OTHER'
}
