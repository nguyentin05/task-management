package com.ntt.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.ntt.authentication.domain.Role;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.RoleResponse;
import com.ntt.authentication.mapper.RoleMapper;
import com.ntt.authentication.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;

    @InjectMocks
    RoleService roleService;

    @Test
    @DisplayName("Success: Lấy danh sách role phân trang thành công, map đúng dữ liệu")
    void getAllRole_ShouldReturnPageResponse() {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").descending());

        Role role1 = Role.builder().name("ADMIN").build();
        Role role2 = Role.builder().name("USER").build();
        Page<Role> pageData = new PageImpl<>(List.of(role1, role2), pageable, 2);

        RoleResponse res1 = RoleResponse.builder().name("ADMIN").build();
        RoleResponse res2 = RoleResponse.builder().name("USER").build();

        when(roleRepository.findAll(any(Pageable.class))).thenReturn(pageData);
        when(roleMapper.toRoleResponse(role1)).thenReturn(res1);
        when(roleMapper.toRoleResponse(role2)).thenReturn(res2);

        PageResponse<RoleResponse> result = roleService.getAllRole(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getCurrentPage()).isEqualTo(page);
        assertThat(result.getPageSize()).isEqualTo(size);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData().get(0).getName()).isEqualTo("ADMIN");

        verify(roleRepository, times(1)).findAll(any(Pageable.class));
        verify(roleMapper, times(2)).toRoleResponse(any(Role.class));
    }

    @Test
    @DisplayName("Success: Database chưa có role nào, trả về danh sách rỗng")
    void getAllRole_Empty_ShouldReturnEmptyPage() {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("name").descending());

        Page<Role> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(roleRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        PageResponse<RoleResponse> result = roleService.getAllRole(page, size);

        assertThat(result).isNotNull();
        assertThat(result.getData()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(roleRepository, times(1)).findAll(any(Pageable.class));
        verify(roleMapper, never()).toRoleResponse(any());
    }
}
