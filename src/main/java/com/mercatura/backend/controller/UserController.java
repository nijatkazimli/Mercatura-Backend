package com.mercatura.backend.controller;

import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.dto.Responses.UserResponse;
import com.mercatura.backend.service.ImageService;
import com.mercatura.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    public UserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @Tag(name = "User")
    @Operation(summary = "Gets all available users.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<UserResponse> getAllUsers(
            @Parameter(description = "Page number for pagination. Default 0.")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size for pagination. Default 200.")
            @RequestParam(defaultValue = "200") Integer size
    ) {
        return userService.getAllUsers(page, size);
    }

    @Tag(name = "User")
    @Operation(summary = "Gets the user with the given ID.")
    @GetMapping("/{id}")
    public UserResponse getUserById(@Parameter(
                                            description = "ID of the user to be retrieved."
                                        ) @PathVariable("id") UUID id) {
        return userService.getUserById(id);
    }

    @Tag(name = "User")
    @Operation(summary = "Adds a profile image to the user with the given ID")
    @PostMapping("/{id}/image")
    public UUIDResponse addProfileImage(
            @Parameter(description = "ID of the user.")
            @PathVariable UUID id,
            @Parameter(description = "The image file.")
            @RequestParam MultipartFile file) throws IOException {
        String imageUrl = imageService.saveImage(file);
        return userService.addProfileImage(id, imageUrl);
    }

    @Tag(name = "User")
    @Operation(summary = "Deletes the user with the given ID.")
    @DeleteMapping("/{id}")
    public void deleteUserById(@Parameter(
                                    description = "ID of the user to be deleted."
                                ) @PathVariable("id") UUID id) {
        userService.deleteUserById(id);
    }
}
