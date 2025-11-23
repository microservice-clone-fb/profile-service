package com.tam.profile.dto.response.file;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
// @Document(collection = "file_mnmt")
public class FileMnmt {
    //    @MongoId
    String id; // ownerId
    String ownerId;

    @Builder.Default
    List<FileDetail> avatar = new ArrayList<>();

    @Builder.Default
    List<FileDetail> wallpaper = new ArrayList<>();

    @Builder.Default
    List<PostFileDetail> post = new ArrayList<>();

    @Builder.Default
    List<StoryFileDetail> story = new ArrayList<>();

    @Builder.Default
    List<MessageFileDetail> message = new ArrayList<>();

    @Data
    @SuperBuilder // 👈 Đổi từ @Builder thành @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDetail {
        String contentType;
        long size;
        String md5Checksum;
        String path;
        String url;
        boolean isActive;
        long uploadedAt;
    }

    @Data
    @SuperBuilder // 👈 Đổi từ @Builder thành @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PostFileDetail extends FileDetail {
        String postId;
    }

    @Data
    @SuperBuilder // 👈 Đổi từ @Builder thành @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class StoryFileDetail extends FileDetail {
        String storyId;
    }

    @Data
    @SuperBuilder // 👈 Đổi từ @Builder thành @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class MessageFileDetail extends FileDetail {
        String messageId;
    }
}
