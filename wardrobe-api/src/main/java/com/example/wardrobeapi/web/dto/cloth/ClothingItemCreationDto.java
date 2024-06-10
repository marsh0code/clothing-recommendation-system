package com.example.wardrobeapi.web.dto.cloth;

import com.example.wardrobeapi.domain.ClothingType;
import com.example.wardrobeapi.domain.StyleType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClothingItemCreationDto {
    @NotBlank(message = "Specify name")
    @Size(min = 4, max = 64, message = "Enter at least 4 and less than 64 characters")
    private String name;

    @NotNull(message = "Specify warmth")
    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double warmth;

    @NotNull(message = "Specify wind resistance")
    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double windResistance;

    @NotNull(message = "Specify water resistance")
    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double waterResistance;

    @NotNull(message = "Specify style coefficient")
    @Min(value = 0, message = "Min value is 0")
    @Max(value = 10, message = "Max value is 10")
    private Double styleCoefficient;

    @NotNull(message = "Specify clothing type")
    private ClothingType clothingType;

    @NotNull(message = "Specify style type")
    private StyleType styleType;
}
