package com.ntt.task_service.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "label")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String boardId;
    String name;
    String color;
    Instant createdAt;
}
