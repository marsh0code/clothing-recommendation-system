package com.example.wardrobeapi.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.domain.ClothingType;
import com.example.wardrobeapi.domain.User;
import com.example.wardrobeapi.exception.*;
import com.example.wardrobeapi.repository.UserRepository;
import com.example.wardrobeapi.security.JwtTokenProvider;
import com.example.wardrobeapi.service.StorageService;
import com.example.wardrobeapi.service.UserService;
import com.example.wardrobeapi.service.recommendation.ClothingRecommendation;
import com.example.wardrobeapi.service.recommendation.RecommendationService;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final StorageService storageService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RecommendationService recommendationService;
    @Value("${aws.bucket}")
    private String bucket;

    @Override
    @Transactional
    public Optional<DecodedJWT> signIn(String username, String password) {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with username %s not found".formatted(username))
        );
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        return jwtTokenProvider.toDecodedJWT(
                jwtTokenProvider.generateToken(user.getId(), username));
    }

    @Override
    @Transactional
    public User signUp(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username %s is already in use".formatted(user.getUsername()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User update(User user, String newPassword) {
        if (isUsernameInUse(user)) {
            throw new UserAlreadyExistsException(
                    "Username %s is already in use".formatted(user.getUsername()));
        }
        if (newPassword != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Optional<List<ClothingItem>> findWardrobeByUsername(String username) {
        return userRepository.findByUsername(username).map(User::getClothingItems).map(List::copyOf);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    @Transactional
    public String addImage(String username, MultipartFile file) {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with username %s not found".formatted(username))
        );
        var imageUrl = UUID.randomUUID().toString();
        try {
            storageService.uploadImage(
                    file.getBytes(),
                    "user-images/%s/%s".formatted(user.getId(), imageUrl),
                    bucket);
        } catch (IOException e) {
            var fileName = file.getOriginalFilename();
            throw new ImageUploadException("Failed to upload image %s".formatted(fileName));
        }
        userRepository.updateImageKeyById(user.getId(), imageUrl);
        return imageUrl;
    }

    @Override
    public Resource getImage(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with username %s not found".formatted(username))
        );
        if (user.getImageUrl() == null || user.getImageUrl().isBlank()) {
            throw new ImageNotFoundException("Image of user with username %s not found".formatted(username));
        }
        return storageService.findByKey(
                "user-images/%s/%s".formatted(user.getId(), user.getImageUrl()),
                bucket);
    }

    @Override
    @Transactional
    public void deleteImage(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with username %s not found".formatted(username))
        );
        if (user.getImageUrl() != null) {
            storageService.deleteByKey(
                    "user-images/%s/%s".formatted(user.getId(), user.getImageUrl()),
                    bucket
            );
            user.setImageUrl(null);
        }
    }

    @Override
    @Transactional
    public List<ClothingRecommendation> getRecommendation(String username, WeatherDto weather, Integer number) {
        var wardrobe = userRepository.findByUsername(username)
                .map(User::getClothingItems)
                .map(List::copyOf)
                .orElseThrow(() -> new UserNotFoundException("User with username %s not found".formatted(username)));

        return Arrays.stream(ClothingType.values())
                .map(clothingType -> wardrobe.stream()
                        .filter(item -> item.getClothingType().equals(clothingType))
                        .toList())
                .flatMap(list -> recommendationService.getRecommendation(list, weather, number).stream())
                .toList();
    }

    private boolean isUsernameInUse(User user) {
        return userRepository.findByUsername(user.getUsername())
                .filter(found -> !found.getId().equals(user.getId())).isPresent();
    }
}
