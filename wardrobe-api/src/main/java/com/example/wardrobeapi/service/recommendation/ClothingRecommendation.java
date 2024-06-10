package com.example.wardrobeapi.service.recommendation;

import com.example.wardrobeapi.domain.ClothingItem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClothingRecommendation {
    private ClothingItem clothingItem;
    private Double percentage;
}
