package com.tam.profile.dto.request;

import java.time.LocalDate;

import com.tam.profile.entity.ContactInfo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {
    String firstName;
    String lastName;
    String avatarId;
    String gender;
    LocalDate dateOfBirth;
    String type;
    String bio;
    ContactInfo contactInfo;
}
