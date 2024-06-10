package com.example.wardrobeapi.web.mapper;

import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.service.recommendation.ClothingRecommendation;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemCreationDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemUpdateDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingRecommendationDto;
import org.mapstruct.*;

@Mapper
public interface ClothingMapper {
    ClothingItemDto toPayload(ClothingItem clothingItem);

    @Mapping(target = "clothingItem", source = "clothingItem")
    ClothingRecommendationDto toPayload(ClothingRecommendation clothingRecommendation);

    ClothingItem toEntity(ClothingItemCreationDto clothingItemCreationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ClothingItem partialUpdate(ClothingItemUpdateDto updateDto, @MappingTarget ClothingItem clothingItem);
}
