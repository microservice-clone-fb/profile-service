package com.tam.profile.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Đại diện cho một địa điểm được lưu trong hệ thống (MongoDB)
 * Có thể là thành phố, quốc gia, trường học, công ty, nơi ở, v.v.
 *
 * Kế thừa từ AuditableBaseDocument để có sẵn các thông tin audit:
 * createdAt, updatedAt, createdBy, updatedBy, history, publicity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "location-entity")
public class LocationInfo extends AuditableBaseDocument {

    @Id
    String id;

    String locationId; // ID từ location-service hoặc hệ thống Neo4j (nếu có)

    String name; // Tên địa điểm (ví dụ: "TP. Hồ Chí Minh", "Đại học Bách Khoa")
    String type; // CITY, COUNTRY, SCHOOL, COMPANY, WORKPLACE, RESIDENCE, etc.
    String address; // Địa chỉ chi tiết
    String city; // Thành phố
    String state; // Tỉnh hoặc bang
    String country; // Quốc gia
    String postalCode; // Mã bưu điện
    String category; // EDUCATION, WORK, RESIDENCE, TRAVEL, etc.
    String description; // Mô tả chi tiết (tùy chọn)

    /**
     * Tọa độ theo định dạng Google Maps (vĩ độ - kinh độ)
     * Sử dụng Double để dễ truyền vào API xác định vị trí thật.
     */
    Double latitude; // Vĩ độ

    Double longitude; // Kinh độ

    /**
     * Mức độ chính xác hoặc độ tin cậy của tọa độ (0-100)
     * Dùng để đánh giá dữ liệu vị trí nếu được nhập thủ công.
     */
    Double accuracy;

    /**
     * Liên kết ngoài: có thể là link Google Maps hoặc map-service
     */
    String mapUrl;

    /**
     * Trạng thái kích hoạt địa điểm (nếu được dùng cho danh mục nội bộ)
     */
    boolean active;
}
