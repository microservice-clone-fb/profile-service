package com.tam.profile.dto.response.relationship;

import java.util.Set;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipUserResponse {
    String userId;
    Set<String> friends;
    Set<String> blocked;
    Set<String> dating;
    Set<String> family;
    Set<String> colleagues;
    Set<String> following;
    Set<String> closeFriends;
    Set<String> anotherUserFollowedIt;
    Set<String> incomingFriendRequests;
    Set<String> outgoingFriendRequests;
}
