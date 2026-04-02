package com.ntt.authentication.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ntt.authentication.domain.Role;
import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.AuthenticationRequest;
import com.ntt.authentication.dto.request.LogoutRequest;
import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.request.TokenRefreshRequest;
import com.ntt.authentication.dto.response.AuthenticationResponse;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.repository.InvalidatedTokenRepository;
import com.ntt.authentication.repository.UserRepository;
import com.ntt.authentication.security.MyUserDetails;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Mock
    private UserRepository userRepository;

    private AuthenticationService authenticationService;

    private final String signerKey = "akfa3yfjifjmcvskdas0ajusnda82mmnm$uj2s0asdjia78571212cijanbsia9hjfyhfy7123456";

    @BeforeEach
    void setUpGlobal() {
        authenticationService = new AuthenticationService(
                authenticationManager,
                invalidatedTokenRepository,
                userRepository,
                signerKey,
                3600L,
                7200L
        );
    }

    @Nested
    @DisplayName("Scenario - Fail: token đã bị blacklist")
    class BlacklistedTokenScenario {
        private String token;

        @BeforeEach
        void setupScenario() throws Exception {
            token = generateCustomTestToken(3600);
        }

        @Test
        @DisplayName("Introspect: trả về false")
        void introspectTest() {
            TokenIntrospectRequest request =
                    TokenIntrospectRequest.builder().token(token).build();

            when(invalidatedTokenRepository.existsById(ArgumentMatchers.anyString()))
                    .thenReturn(true);

            var response = authenticationService.introspect(request);

            assertThat(response.isValid()).isFalse();
        }

        @Test
        @DisplayName("Refresh: trả về lỗi UNAUTHENTICATED")
        void refreshTest() {
            TokenRefreshRequest request =
                    TokenRefreshRequest.builder().token(token).build();

            assertThatThrownBy(() -> authenticationService.refresh(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHENTICATED);
        }
    }

    @Nested
    @DisplayName("Scenario - Fail: token đã hết hạn")
    class ExpiredTokenScenario {
        private String expiredToken;

        @BeforeEach
        void setupScenario() throws Exception {
            expiredToken = generateCustomTestToken(-90000);
        }

        @Test
        @DisplayName("Introspect: trả về false")
        void introspectTest() {
            TokenIntrospectRequest request =
                    TokenIntrospectRequest.builder().token(expiredToken).build();

            IntrospectResponse response = authenticationService.introspect(request);

            assertThat(response.isValid()).isFalse();
        }

        @Test
        @DisplayName("Refresh: trả về lỗi UNAUTHENTICATED")
        void refreshTest() {
            TokenRefreshRequest request =
                    TokenRefreshRequest.builder().token(expiredToken).build();

            assertThatThrownBy(() -> authenticationService.refresh(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHENTICATED);
        }
    }

    @Nested
    @DisplayName("Scenario - Fail: token sai định dạng")
    class InvalidTokenScenario {
        private String invalidToken;

        @BeforeEach
        void setupScenario() {
            invalidToken = "invaid-token";
        }

        @Test
        @DisplayName("Introspect: trả về false")
        void introspectTest() {
            TokenIntrospectRequest request =
                    TokenIntrospectRequest.builder().token(invalidToken).build();

            IntrospectResponse response = authenticationService.introspect(request);

            assertThat(response.isValid()).isFalse();
        }

        @Test
        @DisplayName("Refresh: trả về lỗi UNAUTHENTICATED")
        void refreshTest() {
            TokenRefreshRequest request =
                    TokenRefreshRequest.builder().token(invalidToken).build();

            assertThatThrownBy(() -> authenticationService.refresh(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHENTICATED);
        }

        @Test
        @DisplayName("Logout: log lại lỗi")
        void logoutTest() {
            LogoutRequest request = LogoutRequest.builder().token(invalidToken).build();

            assertThatCode(() -> authenticationService.logout(request)).doesNotThrowAnyException();

            verify(invalidatedTokenRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Authenticate: test hàm authenticate")
    class AuthenticateTest {
        private AuthenticationRequest authRequest;
        private User user;

        static Stream<Arguments> provideAuthExceptionsAndExpectedErrors() {
            return Stream.of(
                    Arguments.of(
                            new InternalAuthenticationServiceException("Không thể xác thực"),
                            ErrorCode.INTERNAL_SERVER_ERROR),
                    Arguments.of(new DisabledException("User đã bị vô hiệu hóa"), ErrorCode.ACCOUNT_DISABLED),
                    Arguments.of(new LockedException("User đã bị khóa"), ErrorCode.ACCOUNT_LOCKED),
                    Arguments.of(new BadCredentialsException("Sai thông tin xác thực"), ErrorCode.INVALID_CREDENTIALS),
                    Arguments.of(new UsernameNotFoundException("Không thấy user"), ErrorCode.INVALID_CREDENTIALS),
                    Arguments.of(new ProviderNotFoundException("Lỗi không thể xử lý"), ErrorCode.UNAUTHENTICATED));
        }

        @BeforeEach
        void setUpAuth() {
            authRequest = AuthenticationRequest.builder()
                    .email("test@example.com")
                    .password("Password123!")
                    .build();

            Role role = new Role();
            role.setName("USER");

            user = User.builder()
                    .id("1")
                    .email("test@example.com")
                    .roles(Set.of(role))
                    .build();
        }

        @Test
        @DisplayName("Success: thông tin xác thực hợp lệ, trả về token")
        void validCredentials_ShouldReturnToken() {
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(new MyUserDetails(user));

            AuthenticationResponse authenticationResponse = authenticationService.authenticate(authRequest);

            assertThat(authenticationResponse).isNotNull();
            assertThat(authenticationResponse.getToken()).isNotBlank();
            assertThat(authenticationResponse.isAuthenticated()).isTrue();

            verify(authenticationManager, times(1)).authenticate(any());
        }

        @ParameterizedTest(name = "Fail: bắt được lỗi {0}, trả về lỗi {1}")
        @MethodSource("provideAuthExceptionsAndExpectedErrors")
        void authFailures_ShouldThrowExpectedAppException(AuthenticationException exception, ErrorCode errorCode) {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(exception);

            assertThatThrownBy(() -> authenticationService.authenticate(authRequest))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", errorCode);
        }
    }

    @Nested
    @DisplayName("Introspect: test hàm introspect")
    class IntrospectTest {
        private String validToken;

        @BeforeEach
        void setUpIntrospect() throws Exception {
            validToken = generateCustomTestToken(3600);
        }

        @Test
        @DisplayName("Success: token hợp lệ, trả về true")
        void introspect_ValidToken_ShouldReturnTrue() {
            TokenIntrospectRequest request =
                    TokenIntrospectRequest.builder().token(validToken).build();

            when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);

            IntrospectResponse response = authenticationService.introspect(request);

            assertThat(response.isValid()).isTrue();
        }
    }

    @Nested
    @DisplayName("Refresh: test hàm refresh")
    class RefreshTest {
        private String token;
        private User user;

        @BeforeEach
        void setUpRefresh() {
            Role role = new Role();
            role.setName("USER");

            user = User.builder()
                    .id("1")
                    .email("test@example.com")
                    .roles(Set.of(role))
                    .build();
        }

        @Test
        @DisplayName("Success: token còn access time và còn refresh time, trả về token")
        void accessValidAndRefreshValid_ShouldReturnToken() throws Exception {
            token = generateCustomTestToken(-600);

            TokenRefreshRequest request =
                    TokenRefreshRequest.builder().token(token).build();

            when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            AuthenticationResponse response = authenticationService.refresh(request);

            assertThat(response.getToken()).isNotBlank();

            verify(invalidatedTokenRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Success: token hết access time và còn refresh time, trả về token")
        void accessExpiredAndRefreshValid_ShouldReturnToken() throws Exception {
            token = generateCustomTestToken(-7200);

            TokenRefreshRequest request =
                    TokenRefreshRequest.builder().token(token).build();

            when(invalidatedTokenRepository.existsById(anyString())).thenReturn(false);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            AuthenticationResponse response = authenticationService.refresh(request);

            assertThat(response.getToken()).isNotBlank();

            verify(invalidatedTokenRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("Logout: test hàm logout")
    class LogoutTest {
        @Test
        @DisplayName("Success: token hợp lệ, thực hiện blacklist token")
        void validToken_ShouldBlacklistToken() throws Exception {
            String token = generateCustomTestToken(3600);

            LogoutRequest request = LogoutRequest.builder().token(token).build();

            authenticationService.logout(request);

            verify(invalidatedTokenRepository, times(1)).save(any());
        }
    }

    private String generateCustomTestToken(long validityInSeconds) throws Exception {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Date expiryTime = new Date(
                Instant.now().plus(validityInSeconds, ChronoUnit.SECONDS).toEpochMilli());
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject("test@example.com")
                .issuer("tin.nguyen.cs05@gmail.com")
                .issueTime(new Date())
                .expirationTime(expiryTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(signerKey.getBytes()));
        return jwsObject.serialize();
    }
}
