package com.tam.profile.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tam.profile.dto.request.ProfileCreationRequest;
import com.tam.profile.dto.request.SearchUserRequest;
import com.tam.profile.dto.request.UpdateProfileRequest;
import com.tam.profile.dto.request.UploadFileRequest;
import com.tam.profile.dto.response.UserProfileResponse;
import com.tam.profile.entity.UserProfile;
import com.tam.profile.exception.AppException;
import com.tam.profile.exception.ErrorCode;
import com.tam.profile.mapper.UserProfileMapper;
import com.tam.profile.repository.UserProfileRepository;
import com.tam.profile.repository.httpclient.FileClient;

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
                .findByContactInfoEmailOrContactInfoPhoneNumber(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
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
        UserProfile userProfile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile = userProfileRepository
                .findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        var profiles = userProfileRepository.findAll();

        return profiles.stream().map(userProfileMapper::toUserProfileReponse).toList();
    }

    public UserProfileResponse getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        var profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));

        return userProfileMapper.toUserProfileReponse(profile);
    }

    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        var profile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));

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
            //            profile.setAvatarId(response.getResult().getUrl());
            //            profile.setOldImages(List.of(OldImageUploaded.builder()
            //                    .id(profile.getAvatarId())
            //                    //                            .imageType(profile.get)
            //                    .build()));

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
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserProfile> userProfiles = null;
        return userProfiles.stream()
                .filter(userProfile -> !userId.equals(userProfile.getUserId()))
                .map(userProfileMapper::toUserProfileReponse)
                .toList();
    }

    public UserProfileResponse findByEmail(String email) {
        return userProfileMapper.toUserProfileReponse(
                userProfileRepository.findByContactInfoEmail(email).orElse(null));
    }
}
