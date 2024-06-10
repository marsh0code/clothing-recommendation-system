package com.example.wardrobeapi.service.recommendation;

import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;

import java.util.List;

public interface RecommendationService {
    List<ClothingRecommendation> getRecommendation(List<ClothingItem> clothingItems, WeatherDto weather, Integer number);
}
