package com.ntt.taskmanagement.auth.internal.security;

import com.ntt.taskmanagement.common.api.ErrorCode;
import com.ntt.taskmanagement.common.exception.AppException;
import com.ntt.taskmanagement.user.UserModuleApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserModuleApi userModuleApi;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userModuleApi.getUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return new SecurityUser(user);
    }
}