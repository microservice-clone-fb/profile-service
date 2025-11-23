package com.tam.profile.dto.response;

import java.time.LocalDate;

import com.tam.profile.dto.response.file.FileMnmt;
import com.tam.profile.entity.ContactInfo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String userId;
    //    String avatarId;
    String gender;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String bio;
    ContactInfo contactInfo;
    FileMnmt fileMnmt;
}
