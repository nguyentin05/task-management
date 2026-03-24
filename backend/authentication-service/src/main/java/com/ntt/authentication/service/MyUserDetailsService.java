package com.ntt.authentication.service;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.repository.UserRepository;
import com.ntt.authentication.security.MyUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        return userRepository
                .findByEmail(email)
                .map(MyUserDetails::new)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
