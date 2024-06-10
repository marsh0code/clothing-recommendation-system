package com.example.wardrobeapi.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.domain.User;
import com.example.wardrobeapi.service.recommendation.ClothingRecommendation;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<DecodedJWT> signIn(String username, String password);

    User signUp(User user);

    User update(User user, String newPassword);

    Optional<User> findByUsername(String username);

    Optional<List<ClothingItem>> findWardrobeByUsername(String username);

    void deleteByUsername(String username);

    String addImage(String username, MultipartFile file);

    Resource getImage(String username);

    void deleteImage(String username);

    List<ClothingRecommendation> getRecommendation(String username, WeatherDto weather, Integer number);
}
