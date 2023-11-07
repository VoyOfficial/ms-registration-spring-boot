package src.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusinessHours {

    private String weekday;
    private Interval interval;

}
