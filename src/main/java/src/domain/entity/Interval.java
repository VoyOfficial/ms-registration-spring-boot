package src.domain.entity;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class Interval {

    private String start;
    private String end;

}
