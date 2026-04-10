package com.ntt.task_service.dto.response;

import java.time.Instant;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColumnResponse {
    String id;
    String projectId;
    String name;
    Double position;
    Instant createdAt;
    Instant updatedAt;
    Boolean isDoneColumn;
    List<ColumnTaskResponse> columnTaskResponses;
}
