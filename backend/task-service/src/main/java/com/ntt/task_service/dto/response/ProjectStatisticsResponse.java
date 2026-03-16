package com.ntt.task_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectStatisticsResponse {
    String projectId;
    String projectName;
    int totalTasks;
    int completedTasks;
    double completionRate;
    int totalMembers;
    int totalColumns;
}
