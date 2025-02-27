package br.voy.application.controller.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import br.voy.domain.entity.BusinessHours;
import br.voy.domain.entity.Interval;

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
