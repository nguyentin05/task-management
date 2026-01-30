package com.ntt.taskmanagement.user.internal.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    @Size(max = 255)
    String email;

    @Column(nullable = false)
    @Size(max = 255)
    String password;

    @Column(name = "first_name")
    @Size(max = 100)
    String firstName;

    @Column(name = "last_name")
    @Size(max = 100)
    String lastName;

    @Column(columnDefinition = "TEXT")
    String avatarUrl;

    @Column(name = "phone_number", length = 20)
    @Size(max = 20)
    String phoneNumber;

    @Column(columnDefinition = "TEXT")
    String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "user_role")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
