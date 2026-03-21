package com.ntt.authentication.domain;

import java.time.Instant;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "invalidated_tokens",
        indexes = {@Index(name = "index_invalidated_tokens_expiry_time", columnList = "expiry_time")})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvalidatedToken {
    @Id
    String id;

    Instant expiryTime;
}
