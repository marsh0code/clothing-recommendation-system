package com.example.wardrobeapi.web.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class WeatherDto {
    private Double temperature;
    private Double windSpeed;
    private Double precipitation;
}
