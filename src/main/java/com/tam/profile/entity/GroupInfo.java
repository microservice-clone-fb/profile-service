package com.tam.profile.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
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
@Document(collection = "group-info")
public class GroupInfo extends AuditableBaseDocument {

    @Id
    String id;

    String ownerId; // userId của chủ sở hữu nhóm
    String name; // tên nhóm
    String description; // mô tả ngắn
    String category; // ví dụ: Tử vi, Âm nhạc, Công nghệ...
    String coverImageId; // ảnh bìa (liên kết file-service)
    String avatarImageId; // ảnh đại diện nhóm
    LocalDate createdDate;
    String rules; // nội quy nhóm (có thể null)
    String location; // nếu là nhóm địa phương, có thể để trống

    boolean verified; // đã xác thực (ví dụ nhóm chính thức)
}
