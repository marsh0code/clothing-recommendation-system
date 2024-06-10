package com.example.wardrobeapi.web.controller;

import com.example.wardrobeapi.service.ClothingService;
import com.example.wardrobeapi.web.dto.ExceptionResponse;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemCreationDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemDto;
import com.example.wardrobeapi.web.dto.cloth.ClothingItemUpdateDto;
import com.example.wardrobeapi.web.mapper.ClothingMapper;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Tag(name = "Clothing controller")
@CrossOrigin
@RestController
@RequestMapping(path = "/clothes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_JPEG_VALUE})
@RequiredArgsConstructor
public class ClothingController {

    private final ClothingService clothingService;
    private final ClothingMapper clothingMapper;

    @PostMapping
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Create new clothing item", responses = {
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClothingItemDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<ClothingItemDto> create(@RequestBody @Valid ClothingItemCreationDto clothingItemCreationDto,
                                                  Principal principal) {
        var created = clothingService.create(clothingMapper.toEntity(clothingItemCreationDto), principal.getName());
        return new ResponseEntity<>(clothingMapper.toPayload(created), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@clothingChecker.isAuthor(#id, #principal.getName())")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Delete clothing item by id", responses = {
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "403", content = @Content)
    })
    public ResponseEntity<Void> deleteById(@PathVariable Long id, Principal principal) {
        clothingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    @PreAuthorize("@clothingChecker.isAuthor(#id, #principal.getName())")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Get clothing item image", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<Resource> getImage(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(clothingService.getImage(id));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@clothingChecker.isAuthor(#id, #principal.getName())")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Add image to clothing item", responses = {
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
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestPart MultipartFile file,
                                              Principal principal) {
        var imageKey = clothingService.addImage(id, file);
        return new ResponseEntity<>(imageKey, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@clothingChecker.isAuthor(#id, #principal.getName())")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Update clothing item by id", responses = {
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClothingItemDto.class))),
            @ApiResponse(responseCode = "400",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    public ResponseEntity<ClothingItemDto> update(@RequestBody @Valid ClothingItemUpdateDto clothingItemUpdateDto,
                                                  @PathVariable Long id, Principal principal) {
        return ResponseEntity.of(clothingService.findById(id)
                .map(menu -> clothingMapper.partialUpdate(clothingItemUpdateDto, menu))
                .map(clothingService::update)
                .map(clothingMapper::toPayload));
    }

    @DeleteMapping("/{id}/image")
    @PreAuthorize("@clothingChecker.isAuthor(#id, #principal.getName())")
    @SecurityRequirement(name = "bearer_token")
    @Operation(summary = "Delete image of clothing item", responses = {
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "403", content = @Content),
            @ApiResponse(responseCode = "404",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<Void> deleteImage(@PathVariable Long id, Principal principal) {
        clothingService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
