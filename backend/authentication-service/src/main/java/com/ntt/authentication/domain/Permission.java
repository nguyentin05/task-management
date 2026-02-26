package com.ntt.authentication.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permission")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {
    @Id
    @Size(max = 255)
    String name;

    @Column(columnDefinition = "TEXT")
    String description;
}
