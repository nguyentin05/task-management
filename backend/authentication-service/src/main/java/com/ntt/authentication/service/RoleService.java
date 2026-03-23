package com.ntt.authentication.service;

import java.util.List;

import com.ntt.authentication.domain.Role;
import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ntt.authentication.dto.response.RoleResponse;
import com.ntt.authentication.mapper.RoleMapper;
import com.ntt.authentication.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public PageResponse<RoleResponse> getAllRole(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").descending());
        Page<Role> pageData = roleRepository.findAll(pageable);

        return PageResponse.<RoleResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(roleMapper::toRoleResponse)
                        .toList())
                .build();
    }
}
