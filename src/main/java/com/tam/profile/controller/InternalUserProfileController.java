package com.tam.profile.controller;

import org.springframework.web.bind.annotation.*;

import com.tam.profile.dto.ApiResponse;
import com.tam.profile.dto.request.ProfileCreationRequest;
import com.tam.profile.dto.request.UpdateProfileRequest;
import com.tam.profile.dto.response.UserProfileResponse;
import com.tam.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }

    @GetMapping("/internal/users/{userId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getByUserId(userId))
                .build();
    }

    @GetMapping("/internal/users/by-any-field/{username}")
    ApiResponse<UserProfileResponse> getProfileByAnyField(@PathVariable String username) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getByAnyField(username))
                .build();
    }

    @PutMapping("/internal/users/{userId}")
    ApiResponse<UserProfileResponse> updateProfile(
            @PathVariable String userId, @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateMyProfile(request))
                .build();
    }

    @GetMapping("/internal/user/exists/{email}")
    ApiResponse<UserProfileResponse> findByEmail(@PathVariable String email) {
        var result = userProfileService.findByEmail(email);
        return ApiResponse.<UserProfileResponse>builder()
                .result(result) //
                .build();
    }
}
