package com.tam.profile.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tam.profile.dto.request.ProfileCreationRequest;
import com.tam.profile.dto.request.SearchUserRequest;
import com.tam.profile.dto.request.UpdateProfileRequest;
import com.tam.profile.dto.request.UploadFileRequest;
import com.tam.profile.dto.response.SearchUserResponse;
import com.tam.profile.dto.response.UserProfileResponse;
import com.tam.profile.dto.response.UserProfileWithRelationshipResponse;
import com.tam.profile.dto.response.relationship.RelationshipUserResponse;
import com.tam.profile.entity.UserProfile;
import com.tam.profile.exception.AppException;
import com.tam.profile.exception.ErrorCode;
import com.tam.profile.mapper.UserProfileMapper;
import com.tam.profile.repository.UserProfileRepository;
import com.tam.profile.repository.httpclient.FileClient;
import com.tam.profile.repository.httpclient.RelationshipClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FileClient fileClient;
    RelationshipClient relationshipClient;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        log.info("Creating profile for userId: {}", request.getUserId());

        // Validate userId
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Check if profile already exists for this userId
        if (userProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new AppException(ErrorCode.USER_PROFILE_ALREADY_EXISTS);
        }

        // Map request to entity
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        // Set default type if not provided
        if (request.getType() == null || request.getType().isEmpty()) {
            userProfile.setType("NORMAL");
        }

        // Save profile
        userProfile = userProfileRepository.save(userProfile);

        log.info(
                "Profile created successfully with id: {} for userId: {}",
                userProfile.getId(),
                userProfile.getUserId());

        // Map to response
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getProfileByUserId(String userId) {
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));

        return userProfileMapper.toUserProfileReponse(profile);
    }

    public UserProfileResponse getByAnyField(String username) {
        UserProfile userProfile = userProfileRepository
                .findByEmailOrPhoneNumber(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        log.info("Found user profile: {}", userProfile.toString());
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));

        userProfileMapper.update(profile, request);
        profile = userProfileRepository.save(profile);

        return userProfileMapper.toUserProfileReponse(profile);
    }

    // Giữ nguyên hàm updateAvatar để dùng sau
    public UserProfileResponse getByUserId(String userId) {
        log.info("🔍 getByUserId() called with userId: {}", userId);
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("❌ Profile not found with userId: {}", userId);
            return new AppException(ErrorCode.USER_PROFILE_NOT_FOUND);
        });
        log.info(
                "✅ Profile found by userId: profileId={}, firstName={}, lastName={}",
                userProfile.getId(),
                userProfile.getFirstName(),
                userProfile.getLastName());
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
        log.info("🔍 getProfile() called with profileId: {}", id);
        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(() -> {
            log.error("❌ Profile not found with profileId: {}", id);
            return new AppException(ErrorCode.USER_PROFILE_NOT_FOUND);
        });
        log.info(
                "✅ Profile found: userId={}, firstName={}, lastName={}",
                userProfile.getUserId(),
                userProfile.getFirstName(),
                userProfile.getLastName());
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getByProfileId(String profileId) {
        log.info("🔍 getByProfileId() called with profileId: {}", profileId);
        return getProfile(profileId);
    }

    public UserProfileWithRelationshipResponse getProfileWithRelationships(String profileId) {
        log.info("🔍 getProfileWithRelationships() called with profileId: {}", profileId);
        UserProfileResponse profile = getProfile(profileId);
        log.info("📊 Getting relationships for userId: {}", profile.getUserId());
        RelationshipUserResponse relationships =
                relationshipClient.getAllRelationship(profile.getUserId()).getResult();

        return UserProfileWithRelationshipResponse.builder()
                .profile(profile)
                .relationships(relationships)
                .build();
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        var profiles = userProfileRepository.findAll();

        return profiles.stream().map(userProfileMapper::toUserProfileReponse).toList();
    }

    public UserProfileResponse getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        log.info("🔍 getMyProfile() called with userId: {}", userId);

        var profile = userProfileRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("❌ My profile not found with userId: {}", userId);
            return new AppException(ErrorCode.USER_PROFILE_NOT_FOUND);
        });

        log.info(
                "✅ My profile found: profileId={}, firstName={}, lastName={}",
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName());
        return userProfileMapper.toUserProfileReponse(profile);
    }

    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        log.info("🔍 updateMyProfile() called with userId: {}", userId);

        var profile = userProfileRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("❌ My profile not found with userId: {}", userId);
            return new AppException(ErrorCode.USER_PROFILE_NOT_FOUND);
        });

        log.info("✅ My profile found, updating...");
        userProfileMapper.update(profile, request);

        return userProfileMapper.toUserProfileReponse(userProfileRepository.save(profile));
    }

    @Deprecated
    public UserProfileResponse updateAvatar(MultipartFile file, String userId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            String userIdUpdate = userId.isEmpty() ? authentication.getName() : userId;

            var profile = userProfileRepository
                    .findByUserId(userIdUpdate)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));

            UploadFileRequest avatarCreationRequest = UploadFileRequest.builder()
                    .ownerId(userId)
                    .relatedIds("AVATAR")
                    .build();

            var response = fileClient.uploadMedia(file, avatarCreationRequest);
            log.info("Is uploaded: {}", response.getResult().getUrl());
            // profile.setAvatarId(response.getResult().getUrl());
            // profile.setOldImages(List.of(OldImageUploaded.builder()
            // .id(profile.getAvatarId())
            // // .imageType(profile.get)
            // .build()));

            return userProfileMapper.toUserProfileReponse(userProfileRepository.save(profile));
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.FILE_NOT_UPLOADED_CORRECTLY) {
                log.error("File not uploaded correctly", e);
                throw new AppException(ErrorCode.FILE_NOT_UPLOADED_CORRECTLY);
            } else if (e.getErrorCode() == ErrorCode.FILE_NOT_FOUND) {
                log.error("File not found", e);
                throw new AppException(ErrorCode.FILE_NOT_FOUND);
            } else if (e.getErrorCode() == ErrorCode.FILE_UPLOAD_FAILED) {
                log.error("File upload failed", e);
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            } else {
                // các lỗi khác (ví dụ 403) để nguyên
                throw e;
            }
        }
    }

    public List<UserProfileResponse> search(SearchUserRequest request) {
        log.info("🔍🔍🔍 SEARCH METHOD CALLED - NEW CODE VERSION 🔍🔍🔍");
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                log.warn("⚠️ Authentication is null in search method");
                return List.of();
            }

            var userId = authentication.getName();
            if (userId == null || userId.isEmpty()) {
                log.warn("⚠️ UserId is null or empty in search method");
                return List.of();
            }

            // Validate request
            if (request == null) {
                log.warn("Search request is null for user: {}", userId);
                return List.of();
            }

            // Validate keyword
            if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
                log.info("Search with empty keyword for user: {}", userId);
                return List.of(); // Return empty list if keyword is empty
            }

            String keyword = request.getKeyword().trim();
            log.info("Searching for keyword: '{}' by user: {}", keyword, userId);

            // Use searchByName from repository (MongoDB query optimization)
            List<UserProfile> matchedProfiles;
            try {
                matchedProfiles = userProfileRepository.searchByName(keyword);
                log.info(
                        "🔍 searchByName() called with keyword: '{}', result: {}",
                        keyword,
                        matchedProfiles != null ? matchedProfiles.size() + " profiles" : "NULL");
            } catch (Exception e) {
                log.error("❌ Error calling userProfileRepository.searchByName(): {}", e.getMessage(), e);
                return List.of();
            }

            // Null safety check - Spring Data should never return null, but be defensive
            if (matchedProfiles == null) {
                log.error(
                        "⚠️ CRITICAL: userProfileRepository.searchByName() returned null! This should never happen. Returning empty list.");
                return List.of();
            }

            log.info("✅ Found {} profiles matching keyword '{}' from database", matchedProfiles.size(), keyword);

            // Additional safety: ensure list is not null before streaming
            if (matchedProfiles == null || matchedProfiles.isEmpty()) {
                log.info("No profiles found matching keyword: '{}'", keyword);
                return List.of();
            }

            // Filter out current user and null profiles
            List<UserProfile> filteredProfiles = matchedProfiles.stream()
                    .filter(profile -> {
                        if (profile == null) {
                            log.warn("⚠️ Found null profile in database!");
                            return false;
                        }
                        if (profile.getUserId() == null) {
                            log.warn("⚠️ Found profile with null userId!");
                            return false;
                        }
                        boolean isNotCurrentUser = !userId.equals(profile.getUserId());
                        if (!isNotCurrentUser) {
                            log.debug(
                                    "Excluding current user: {} (firstName: {}, lastName: {})",
                                    profile.getUserId(),
                                    profile.getFirstName(),
                                    profile.getLastName());
                        }
                        return isNotCurrentUser;
                    })
                    .toList();

            log.info(
                    "Found {} matching profiles (excluding current user) for keyword: '{}'",
                    filteredProfiles.size(),
                    keyword);

            // Map to response
            if (filteredProfiles.isEmpty()) {
                return List.of();
            }

            return filteredProfiles.stream()
                    .map(userProfileMapper::toUserProfileReponse)
                    .toList();
        } catch (Exception e) {
            log.error("❌ Unexpected error in search method: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Search users với relationship status (giống Facebook thực tế)
     * Trả về profile kèm relationship status để UI biết đã là bạn chưa, đã gửi request chưa
     */
    public List<SearchUserResponse> searchWithRelationships(SearchUserRequest request) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                log.warn("⚠️ Authentication is null in searchWithRelationships method");
                return List.of();
            }

            var currentUserId = authentication.getName();
            if (currentUserId == null || currentUserId.isEmpty()) {
                log.warn("⚠️ UserId is null or empty in searchWithRelationships method");
                return List.of();
            }

            // Validate request
            if (request == null) {
                log.warn("Search request is null for user: {}", currentUserId);
                return List.of();
            }

            // Validate keyword
            if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
                return List.of();
            }

            String keyword = request.getKeyword().trim();

            // Get all profiles (vì cần search không dấu nên sẽ filter trong Java)
            List<UserProfile> allProfiles;
            try {
                allProfiles = userProfileRepository.findAll();
            } catch (Exception ex) {
                log.error(
                        "❌ Error calling userProfileRepository.findAll() in searchWithRelationships: {}",
                        ex.getMessage(),
                        ex);
                return List.of();
            }

            // Null safety check
            if (allProfiles == null) {
                log.warn(
                        "⚠️ userProfileRepository.findAll() returned null in searchWithRelationships! Returning empty list.");
                return List.of();
            }

            if (allProfiles.isEmpty()) {
                log.info("No profiles found in database");
                return List.of();
            }

            // Get current user's relationships để check status
            RelationshipUserResponse currentUserRelationships = null;
            try {
                currentUserRelationships =
                        relationshipClient.getAllRelationship(currentUserId).getResult();
            } catch (Exception e) {
                log.warn("Failed to get relationships for user {}: {}", currentUserId, e.getMessage());
            }

            final RelationshipUserResponse relationships = currentUserRelationships;

            // Filter và map với relationship status
            List<SearchUserResponse> results = allProfiles.stream()
                    .filter(profile -> {
                        if (profile == null || profile.getUserId() == null) {
                            return false;
                        }
                        return !currentUserId.equals(profile.getUserId());
                    })
                    .filter(profile -> matchesSearch(profile, keyword))
                    .map(profile -> {
                        UserProfileResponse profileResponse = userProfileMapper.toUserProfileReponse(profile);
                        return buildSearchUserResponse(profileResponse, relationships, currentUserId);
                    })
                    .toList();

            return results;
        } catch (Exception e) {
            log.error("❌ Unexpected error in searchWithRelationships method: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Build SearchUserResponse với relationship status
     */
    private SearchUserResponse buildSearchUserResponse(
            UserProfileResponse profile, RelationshipUserResponse currentUserRelationships, String currentUserId) {

        if (currentUserRelationships == null) {
            return SearchUserResponse.builder()
                    .profile(profile)
                    .relationshipStatus("NONE")
                    .isFriend(false)
                    .hasOutgoingFriendRequest(false)
                    .hasIncomingFriendRequest(false)
                    .isFollowing(false)
                    .isFollower(false)
                    .build();
        }

        String targetUserId = profile.getUserId();
        boolean isFriend = currentUserRelationships.getFriends() != null
                && currentUserRelationships.getFriends().contains(targetUserId);
        boolean hasOutgoingRequest = currentUserRelationships.getOutgoingFriendRequests() != null
                && currentUserRelationships.getOutgoingFriendRequests().contains(targetUserId);
        boolean hasIncomingRequest = currentUserRelationships.getIncomingFriendRequests() != null
                && currentUserRelationships.getIncomingFriendRequests().contains(targetUserId);
        boolean isFollowing = currentUserRelationships.getFollowing() != null
                && currentUserRelationships.getFollowing().contains(targetUserId);
        boolean isFollower = currentUserRelationships.getAnotherUserFollowedIt() != null
                && currentUserRelationships.getAnotherUserFollowedIt().contains(targetUserId);

        // Determine relationship status
        String relationshipStatus;
        if (isFriend) {
            relationshipStatus = "FRIEND";
        } else if (hasOutgoingRequest) {
            relationshipStatus = "PENDING_OUTGOING";
        } else if (hasIncomingRequest) {
            relationshipStatus = "PENDING_INCOMING";
        } else if (isFollowing) {
            relationshipStatus = "FOLLOWING";
        } else if (isFollower) {
            relationshipStatus = "FOLLOWER";
        } else {
            relationshipStatus = "NONE";
        }

        return SearchUserResponse.builder()
                .profile(profile)
                .relationshipStatus(relationshipStatus)
                .isFriend(isFriend)
                .hasOutgoingFriendRequest(hasOutgoingRequest)
                .hasIncomingFriendRequest(hasIncomingRequest)
                .isFollowing(isFollowing)
                .isFollower(isFollower)
                .build();
    }

    /**
     * Check if profile matches search keyword (fuzzy, case-insensitive, diacritics-insensitive)
     *
     * Hỗ trợ nhiều trường hợp tìm kiếm:
     * 1. Tìm theo lastName: "Nguyen" → tìm được "Nguyễn Văn A", "Nguyễn Thị B"
     * 2. Tìm theo firstName: "Van" → tìm được "Nguyễn Văn A", "Trần Văn C"
     * 3. Tìm theo cả 2: "Nguyen Van" → tìm được "Nguyễn Văn A"
     * 4. Tìm partial: "Nguy" → tìm được tất cả có chứa "Nguy"
     * 5. Hỗ trợ không dấu: "Nguyen" tìm được "Nguyễn"
     * 6. Case-insensitive: "nguyen" = "Nguyen" = "NGUYEN"
     */
    private boolean matchesSearch(UserProfile profile, String keyword) {
        if (profile == null || keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        String searchKeyword = keyword.trim();
        String firstName =
                profile.getFirstName() != null ? profile.getFirstName().trim() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName().trim() : "";

        // Case 1: Tìm theo lastName
        if (!lastName.isEmpty()) {
            if (com.tam.profile.utils.VietnameseTextUtils.containsIgnoreDiacritics(lastName, searchKeyword)) {
                return true;
            }
        }

        // Case 2: Tìm theo firstName
        if (!firstName.isEmpty()) {
            if (com.tam.profile.utils.VietnameseTextUtils.containsIgnoreDiacritics(firstName, searchKeyword)) {
                return true;
            }
        }

        // Case 3: Tìm theo fullName (firstName + lastName)
        String fullName = firstName + " " + lastName;
        if (!fullName.trim().isEmpty()) {
            if (com.tam.profile.utils.VietnameseTextUtils.containsIgnoreDiacritics(fullName, searchKeyword)) {
                return true;
            }
        }

        // Case 4: Tìm theo lastName + firstName (ngược lại)
        // Ví dụ: user có firstName="Văn", lastName="Nguyễn"
        // Search "Nguyen Van" vẫn tìm được
        String reverseFullName = lastName + " " + firstName;
        if (!reverseFullName.trim().isEmpty()) {
            if (com.tam.profile.utils.VietnameseTextUtils.containsIgnoreDiacritics(reverseFullName, searchKeyword)) {
                return true;
            }
        }

        return false;
    }

    public UserProfileResponse findByEmail(String email) {
        return userProfileMapper.toUserProfileReponse(
                userProfileRepository.findByContactInfoEmail(email).orElse(null));
    }
}
