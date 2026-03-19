package com.ntt.authentication.service;

import java.util.List;

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

    public List<RoleResponse> getAllRole() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }
}
