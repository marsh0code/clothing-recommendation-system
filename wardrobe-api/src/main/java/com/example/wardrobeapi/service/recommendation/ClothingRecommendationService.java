package com.example.wardrobeapi.service.recommendation;

import com.example.wardrobeapi.domain.ClothingItem;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ClothingRecommendationService implements RecommendationService {

    @Override
    public List<ClothingRecommendation> getRecommendation(List<ClothingItem> clothingItems, WeatherDto weather, Integer number) {
        var waterResistance = normalize(weather.getPrecipitation(), 0.0, 100.0, 0.0, 10.0);
        var windResistance = normalize(weather.getWindSpeed(), 0.0, 15.0, 0.0, 10.0);
        var warmth = normalize(weather.getTemperature(), -50.0, 50.0, 10.0, 0.0);
        return clothingItems.stream()
                .map(item -> new ClothingRecommendation(item,
                        calculateMatchPercentage(calculateDistance(item, warmth, windResistance, waterResistance))))
                .sorted(Comparator.comparingDouble(item -> -item.getPercentage()))
                .limit(number)
                .toList();
    }

    private double calculateDistance(ClothingItem clothingItem, double warmth, double windResistance, double waterResistance) {
        return Math.sqrt(
                Math.pow(warmth - clothingItem.getWarmth(), 2) +
                        Math.pow(windResistance - clothingItem.getWaterResistance(), 2) +
                        0.1 * Math.pow(waterResistance - clothingItem.getWindResistance(), 2) +
                        0.05 * Math.pow(10 - clothingItem.getStyleCoefficient(), 2)
        );
    }

    private double calculateMatchPercentage(double distance) {
        double maxDistance = Math.sqrt(2 * Math.pow(10, 2) + 0.05 * Math.pow(10, 2) + 0.1 * Math.pow(10, 2));
        return (1 - (distance / maxDistance)) * 100;
    }

    private double normalize(double oldValue, double oldMin, double oldMax, double newMin, double newMax) {
        if (oldValue > oldMax) {
            oldValue = oldMax;
        } else if (oldValue < oldMin) {
            oldValue = oldMin;
        }
        return (((oldValue - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
    }
}
