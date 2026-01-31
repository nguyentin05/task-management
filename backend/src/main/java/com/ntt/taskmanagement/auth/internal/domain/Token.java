package com.ntt.taskmanagement.auth.internal.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token", indexes = {
        @Index(name = "idx_token_user_id", columnList = "user_id"),
        @Index(name = "idx_token_token", columnList = "token")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, length = 512, nullable = false)
    String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 50)
    @Builder.Default
    TokenType tokenType = TokenType.BEARER;

    @Column(nullable = false)
    @Builder.Default
    boolean revoked = false;

    @Column(nullable = false)
    @Builder.Default
    boolean expired = false;

    @Column(name = "user_id", nullable = false)
    Long userId;
}
