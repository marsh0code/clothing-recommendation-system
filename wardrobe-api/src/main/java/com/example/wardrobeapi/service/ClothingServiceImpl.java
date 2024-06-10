package com.example.wardrobeapi.service;

import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.exception.ClothingItemNotFound;
import com.example.wardrobeapi.exception.ImageNotFoundException;
import com.example.wardrobeapi.exception.ImageUploadException;
import com.example.wardrobeapi.repository.ClothingRepository;
import com.example.wardrobeapi.repository.UserRepository;
import com.example.wardrobeapi.service.ClothingService;
import com.example.wardrobeapi.service.StorageService;
import com.example.wardrobeapi.service.recommendation.ClothingRecommendation;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClothingServiceImpl implements ClothingService {

    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    @Value("${aws.bucket}")
    private String bucket;

    @Override
    @Transactional
    public ClothingItem create(ClothingItem clothingItem, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username %s".formatted(username)));
        user.addClothing(clothingItem);
        return clothingRepository.save(clothingItem);
    }

    @Override
    public ClothingItem update(ClothingItem clothingItem) {
        return clothingRepository.save(clothingItem);
    }

    @Override
    public Optional<ClothingItem> findById(Long id) {
        return clothingRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        clothingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public String addImage(Long clothingItemId, MultipartFile file) {
        if (!clothingRepository.existsById(clothingItemId)) {
            throw new ClothingItemNotFound("Clothing item with id %s not found".formatted(clothingItemId));
        }
        var imageKey = UUID.randomUUID().toString();
        try {
            storageService.uploadImage(
                    file.getBytes(),
                    "clothing-images/%s/%s".formatted(clothingItemId, imageKey),
                    bucket);
        } catch (IOException e) {
            var fileName = file.getOriginalFilename();
            throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
        }
        clothingRepository.updateImageKeyById(clothingItemId, imageKey);
        return imageKey;
    }

    @Override
    public Resource getImage(Long recipeId) {
        var recipe = clothingRepository.findById(recipeId).orElseThrow(
                () -> new ClothingItemNotFound("Clothing item with id %s not found".formatted(recipeId)));
        if (recipe.getImageUrl() == null || recipe.getImageUrl().isBlank()) {
            throw new ImageNotFoundException("Image of clothing item with id %s not found".formatted(recipeId));
        }
        return storageService.findByKey(
                "clothing-images/%s/%s".formatted(recipeId, recipe.getImageUrl()),
                bucket);
    }

    @Override
    @Transactional
    public void deleteImage(Long clothingItemId) {
        var clothingItem = clothingRepository.findById(clothingItemId).orElseThrow(
                () -> new ClothingItemNotFound("Clothing item with id %s not found".formatted(clothingItemId)));
        if (clothingItem.getImageUrl() != null) {
            storageService.deleteByKey(
                    "clothing-images/%s/%s".formatted(clothingItemId, clothingItem.getImageUrl()),
                    bucket
            );
            clothingItem.setImageUrl(null);
        }
    }

}
