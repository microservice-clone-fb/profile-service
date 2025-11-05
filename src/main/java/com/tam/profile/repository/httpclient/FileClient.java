package com.tam.profile.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.tam.profile.configuration.AuthenticationRequestInterceptor;
import com.tam.profile.dto.ApiResponse;
import com.tam.profile.dto.request.UploadFileRequest;
import com.tam.profile.dto.response.FileResponse;

@FeignClient(
        name = "file-service",
        url = "http://localhost:8084",
        configuration = {AuthenticationRequestInterceptor.class})
public interface FileClient {
    @PostMapping(
            value = "/file/media/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<FileResponse> uploadMedia(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") UploadFileRequest request // Đổi từ @RequestBody thành @RequestPart
            );
}
