package com.tam.profile.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tam.profile.dto.ApiResponse;
import com.tam.profile.dto.request.SearchUserRequest;
import com.tam.profile.dto.request.UpdateProfileRequest;
import com.tam.profile.dto.response.SearchUserResponse;
import com.tam.profile.dto.response.UserProfileResponse;
import com.tam.profile.dto.response.UserProfileWithRelationshipResponse;
import com.tam.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/users")
    ApiResponse<UserProfileResponse> getProfile(
            @RequestParam(required = false) String userId, @RequestParam(required = false) String profileId) {

        // Validate: phải có ít nhất 1 trong 2
        if (userId == null && profileId == null) {
            throw new IllegalArgumentException("Either userId or profileId must be provided");
        }

        // Ưu tiên userId nếu có cả 2
        if (userId != null) {
            log.info("🔍 GET /users - Request to get profile by userId: {}", userId);
            return ApiResponse.<UserProfileResponse>builder()
                    .result(userProfileService.getByUserId(userId))
                    .build();
        }

        log.info("🔍 GET /users - Request to get profile by profileId: {}", profileId);
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getByProfileId(profileId))
                .build();
    }

    @GetMapping("/users/{profileId}/relationships")
    ApiResponse<UserProfileWithRelationshipResponse> getProfileWithRelationships(@PathVariable String profileId) {
        log.info("🔍 GET /users/{}/relationships - Request to get profile with relationships by profileId", profileId);
        return ApiResponse.<UserProfileWithRelationshipResponse>builder()
                .result(userProfileService.getProfileWithRelationships(profileId))
                .build();
    }

    @GetMapping("/users/all")
    ApiResponse<List<UserProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileService.getAllProfiles())
                .build();
    }

    @GetMapping("/users/my-profile")
    ApiResponse<UserProfileResponse> getMyProfile() {
        log.info("🔍 GET /users/my-profile - Request to get my profile");
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyProfile())
                .build();
    }

    @PutMapping("/users/my-profile")
    ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        log.info("🔍 PUT /users/my-profile - Request to update my profile");
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateMyProfile(request))
                .build();
    }

    @PutMapping("/users/avatar")
    ApiResponse<UserProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateAvatar(file, ""))
                .build();
    }

    @PostMapping("/users/search")
    ApiResponse<List<UserProfileResponse>> search(@RequestBody SearchUserRequest request) {
        log.info(
                "🔍 POST /users/search - Request to search users with keyword: '{}'",
                request != null && request.getKeyword() != null ? request.getKeyword() : "null");
        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(userProfileService.search(request))
                .build();
    }

    /**
     * Search users với relationship status (giống Facebook thực tế)
     * Trả về profile kèm relationship status để biết đã là bạn chưa, đã gửi request chưa
     */
    @PostMapping("/users/search-with-relationships")
    ApiResponse<List<SearchUserResponse>> searchWithRelationships(@RequestBody SearchUserRequest request) {
        log.info(
                "🔍 POST /users/search-with-relationships - Request to search users with relationships, keyword: '{}'",
                request != null && request.getKeyword() != null ? request.getKeyword() : "null");
        return ApiResponse.<List<SearchUserResponse>>builder()
                .result(userProfileService.searchWithRelationships(request))
                .build();
    }
}
