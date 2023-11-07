package src.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntervalHours {

    private String id;
    private String startTime;
    private String endTime;
}
