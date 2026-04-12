package com.ntt.profile_service.controller.internal;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.profile_service.dto.response.ApiResponse;
import com.ntt.profile_service.dto.response.ProfileSearchResponse;
import com.ntt.profile_service.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InternalProfileController {
    ProfileService profileService;

    @GetMapping("/profiles/search")
    ApiResponse<List<ProfileSearchResponse>> searchByUserIds(@RequestParam(value = "userIds") List<String> userIds) {
        return ApiResponse.<List<ProfileSearchResponse>>builder().result(profileService.searchByUserIds(userIds)).build();
    }
}
