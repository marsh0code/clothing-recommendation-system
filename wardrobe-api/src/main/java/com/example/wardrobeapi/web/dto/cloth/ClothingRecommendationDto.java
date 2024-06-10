package com.example.wardrobeapi.web.dto.cloth;

import lombok.Data;

@Data
public class ClothingRecommendationDto {
    private ClothingItemDto clothingItem;
    private Double percentage;
}
