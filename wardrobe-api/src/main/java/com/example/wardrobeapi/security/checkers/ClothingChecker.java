package com.example.wardrobeapi.security.checkers;

import com.example.wardrobeapi.exception.ClothingItemNotFound;
import com.example.wardrobeapi.repository.ClothingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClothingChecker {
    private final ClothingRepository clothingRepository;

    public boolean isAuthor(Long id, String username) {
        if (id == null || username == null) {
            return false;
        }
        var clothingItem = clothingRepository.findById(id).orElseThrow(
                () -> new ClothingItemNotFound("Clothing item with id %s not found".formatted(id))
        );
        return clothingItem.getUser().getUsername().equals(username);
    }
}
