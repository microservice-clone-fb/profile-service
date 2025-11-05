package com.tam.profile.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.*;
import lombok.experimental.SuperBuilder;

// @Deprecated

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AuditableBaseDocument {

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant lastUpdatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastUpdatedBy;

    private int publicity; // 0: private, 1: friends, 2: public

    private String history;

    public void addHistoryEntry(String entry) {
        if (this.history == null) {
            this.history = entry;
        } else {
            this.history = this.history + "\n" + entry;
        }
    }
}
