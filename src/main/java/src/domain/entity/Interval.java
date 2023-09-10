package src.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Interval {

    private String startTime;
    private String endTime;

}
