package com.tam.profile.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "advanced-profile-info")
public class AdvancedProfileInfo extends UserProfile {

    String businessEmail; // email liên hệ công việc
    String website; // trang web cá nhân / thương hiệu
    String category; // ví dụ: Người sáng tạo nội dung, Nghệ sĩ, Doanh nhân
    String tagline; // slogan ngắn
    String location; // nơi làm việc hoặc hoạt động chính
    String coverImageId; // ảnh bìa chuyên nghiệp (file-service)
    String avatarImageId; // ảnh đại diện chuyên nghiệp (file-service)
    String organization; // tổ chức hoặc công ty liên kết

    boolean verified; // tick xanh / đã xác minh
}
