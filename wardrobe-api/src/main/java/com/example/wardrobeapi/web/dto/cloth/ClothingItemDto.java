package com.example.wardrobeapi.web.dto.cloth;

import com.example.wardrobeapi.domain.ClothingType;
import com.example.wardrobeapi.domain.StyleType;
import lombok.Data;

@Data
public class ClothingItemDto {
    private Long id;
    private String name;
    private String imageUrl;
    private Double warmth;
    private Double windResistance;
    private Double waterResistance;
    private Double styleCoefficient;
    private ClothingType clothingType;
    private StyleType styleType;
}
