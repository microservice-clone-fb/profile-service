package com.tam.profile.dto.response;

import com.tam.profile.dto.response.relationship.RelationshipUserResponse;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileWithRelationshipResponse {
    UserProfileResponse profile;
    RelationshipUserResponse relationships;
}

