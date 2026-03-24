package com.ntt.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.ntt.authentication.domain.User;
import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    MyUserDetailsService myUserDetailsService;

    @Test
    @DisplayName("Success: Tìm thấy user, trả về UserDetails")
    void loadUserByUsername_UserFound_ShouldReturnUserDetails() {
        String email = "tin@example.com";
        User mockUser = User.builder().email(email).password("hashedPass").build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails result = myUserDetailsService.loadUserByUsername(email);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo("hashedPass");
    }

    @Test
    @DisplayName("Fail: Không tìm thấy user, ném lỗi AppException(USER_NOT_FOUND)")
    void loadUserByUsername_UserNotFound_ShouldThrowException() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
