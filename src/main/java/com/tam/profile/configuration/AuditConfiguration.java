package com.tam.profile.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tam.profile.service.AuditServices;
import com.tam.profile.utils.AuditListener;

// @Deprecated
// @Deprecated
@Configuration
public class AuditConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditServices.class)
    public AuditServices auditService() {
        return new AuditServices();
    }

    @Bean
    @ConditionalOnMissingBean(AuditListener.class)
    public AuditListener auditEntityListener(AuditServices auditServices) {
        return new AuditListener(auditServices);
    }
}
