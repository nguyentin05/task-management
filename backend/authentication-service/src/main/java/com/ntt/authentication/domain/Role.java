package com.ntt.authentication.domain;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    String name;

    @Column(columnDefinition = "TEXT")
    String description;
}
