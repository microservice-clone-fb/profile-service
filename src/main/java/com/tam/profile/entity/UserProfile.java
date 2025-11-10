package com.tam.profile.entity;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "user-profile-info")
public class UserProfile extends AuditableBaseDocument {
    @Id
    String id;

    String userId; // This is the ID referencing a user from identity-service
    //    String avatarId; // This is an ID referencing an avatar image from file-service
    //    String wallAvatar; // This is an ID referencing a wall image from file-service
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String gender;
    String type; // normal, famous, admin, enterprise
    String bio;
    ContactInfo contactInfo;
    boolean professionalModeEnabled; // true = đang bật chế độ page chuyên nghiệp

    //    List<OldImageUploaded> oldImages;
}
