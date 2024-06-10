package com.example.wardrobeapi.web.controller;

import com.example.wardrobeapi.service.UserService;
import com.example.wardrobeapi.web.dto.ExceptionResponse;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingRecommendationDto;
import com.example.wardrobeapi.web.dto.user.UserDto;
import com.example.wardrobeapi.web.dto.user.UserUpdateDto;
import com.example.wardrobeapi.web.dto.weather.WeatherDto;
import com.example.wardrobeapi.web.mapper.ClothingMapper;
import com.example.wardrobeapi.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "User controller")
@CrossOrigin
@RestController
@RequestMapping(path = "/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE})
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ClothingMapper clothingMapper;

    @GetMapping("/self")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Get self", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    public ResponseEntity<UserDto> findSelf(Principal principal) {
        return ResponseEntity.of(userService.findByUsername(principal.getName()).map(userMapper::toPayload));
    }

    @GetMapping("/self/wardrobe")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Get self wardrobe", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClothingItemDto.class))),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    public ResponseEntity<List<ClothingItemDto>> findSelfWardrobe(Principal principal) {
        return ResponseEntity.of(userService.findWardrobeByUsername(principal.getName())
                .map(items -> items.stream().map(clothingMapper::toPayload).toList()));
    }

    @GetMapping("/self/wardrobe/recommendation")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Get self wardrobe recommendation", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClothingItemDto.class))),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    public ResponseEntity<List<ClothingRecommendationDto>> getSelfWardrobeRecommendation(@RequestParam(name = "temperature") Double temperature,
                                                                                         @RequestParam(name = "wind-speed") Double windSpeed,
                                                                                         @RequestParam(name = "precipitation") Double precipitation,
                                                                                         @RequestParam(name = "number") Integer number,
                                                                                         Principal principal) {
        return ResponseEntity.ok(userService.getRecommendation(principal.getName(), new WeatherDto(temperature, windSpeed, precipitation), number)
                .stream().map(clothingMapper::toPayload)
                .toList());
    }

    @GetMapping("/self/image")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Get self image", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<Resource> getImage(Principal principal) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(userService.getImage(principal.getName()));
    }

    @PostMapping("/self/image")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Add self image", responses = {
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<String> uploadImage(@RequestPart MultipartFile file,
                                              Principal principal) {
        var imageKey = userService.addImage(principal.getName(), file);
        return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
    }

    @PatchMapping("/self")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Update self", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    public ResponseEntity<UserDto> update(@RequestBody @Valid UserUpdateDto userDto,
                                          Principal principal) {
        return ResponseEntity.of(userService.findByUsername(principal.getName())
                .map(user -> userMapper.partialUpdate(userDto, user))
                .map(user -> userService.update(user, userDto.getNewPassword()))
                .map(userMapper::toPayload));
    }


    @DeleteMapping("/self")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Delete self", responses = {
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "403", content = @Content)
    })
    public ResponseEntity<Void> deleteByUsername(Principal principal) {
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/self/image")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Delete self image", responses = {
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "403", content = @Content),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<Void> deleteImage(Principal principal) {
        userService.deleteImage(principal.getName());
        return ResponseEntity.noContent().build();
    }
}
