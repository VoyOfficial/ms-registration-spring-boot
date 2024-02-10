package src.application.controller.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import src.domain.entity.BusinessHours;
import src.domain.entity.Interval;

import java.util.Map;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessHoursResponse {

    private String day;
    private Interval interval;

    public static BusinessHoursResponse toBusinessHoursResponse(BusinessHours businessHours) {

        return BusinessHoursResponse
                .builder()
                .day(businessHours.getDay())
                .interval(businessHours.getInterval())
                .build();

    }

}
