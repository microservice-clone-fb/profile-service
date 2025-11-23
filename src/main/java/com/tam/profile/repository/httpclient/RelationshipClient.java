package com.tam.profile.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tam.profile.configuration.AuthenticationRequestInterceptor;
import com.tam.profile.dto.ApiResponse;
import com.tam.profile.dto.response.relationship.RelationshipUserResponse;

@FeignClient(
        name = "relationship-service",
        url = "${app.services.relationship}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface RelationshipClient {

    @GetMapping("/users/all-relationship/{userId}")
    ApiResponse<RelationshipUserResponse> getAllRelationship(@PathVariable String userId);
}
