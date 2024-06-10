package com.example.wardrobeapi.service;

import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.exception.ClothingItemNotFound;
import com.example.wardrobeapi.exception.ImageNotFoundException;
import com.example.wardrobeapi.exception.ImageUploadException;
import com.example.wardrobeapi.service.recommendation.ClothingRecommendation;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClothingService {
    ClothingItem create(ClothingItem clothingItem, String username);

    ClothingItem update(ClothingItem clothingItem);

    Optional<ClothingItem> findById(Long id);

    void deleteById(Long id);

    String addImage(Long clothingItemId, MultipartFile file);

    Resource getImage(Long recipeId);

    void deleteImage(Long clothingItemId);
}
