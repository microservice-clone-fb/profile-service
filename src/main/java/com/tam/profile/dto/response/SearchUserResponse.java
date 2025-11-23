package com.tam.profile.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUserResponse {
    UserProfileResponse profile;

    // Relationship status với user hiện tại
    String relationshipStatus; // "FRIEND", "PENDING_OUTGOING", "PENDING_INCOMING", "FOLLOWING", "FOLLOWER", "NONE"

    // Các fields tiện lợi để UI dễ check
    boolean isFriend;
    boolean hasOutgoingFriendRequest; // Đã gửi yêu cầu kết bạn
    boolean hasIncomingFriendRequest; // Đã nhận yêu cầu kết bạn
    boolean isFollowing;
    boolean isFollower;
}
