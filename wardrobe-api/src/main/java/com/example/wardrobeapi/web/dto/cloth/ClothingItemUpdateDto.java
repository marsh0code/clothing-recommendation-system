package com.example.wardrobeapi.web.dto.cloth;

import com.example.wardrobeapi.domain.ClothingType;
import com.example.wardrobeapi.domain.StyleType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClothingItemUpdateDto {
    @Size(min = 4, max = 64, message = "Enter at least 4 and less than 64 characters")
    private String name;

    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double warmth;

    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double windResistance;

    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double waterResistance;

    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double styleCoefficient;

    private ClothingType clothingType;

    private StyleType styleType;
}
