package com.tam.profile.utils;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.tam.profile.entity.AuditableBaseDocument;
import com.tam.profile.service.AuditServices;

import lombok.RequiredArgsConstructor;

/**
 * JPA Entity Listener cho audit functionality
 * Không dùng @Component vì sẽ được inject thủ công
 */

// @Deprecated
@Component
@RequiredArgsConstructor
public class AuditListener extends AbstractMongoEventListener<AuditableBaseDocument> {

    private final AuditServices auditService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<AuditableBaseDocument> event) {
        AuditableBaseDocument entity = event.getSource();
        Instant now = Instant.now();
        String currentUser = auditService.getCurrentUsername();

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(currentUser);

            String historyEntry = auditService.createHistoryEntry(currentUser, "CREATED", now);
            entity.addHistoryEntry(historyEntry);
        }

        entity.setLastUpdatedAt(now);
        entity.setLastUpdatedBy(currentUser);

        String historyEntry = auditService.createHistoryEntry(currentUser, "UPDATED", now);
        entity.addHistoryEntry(historyEntry);
    }
}
