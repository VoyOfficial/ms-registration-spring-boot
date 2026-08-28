package br.voy.application.controller.response;

import br.voy.domain.entity.BusinessHours;
import br.voy.domain.entity.Interval;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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

        return BusinessHoursResponse.builder()
                .day(businessHours.getDay())
                .interval(businessHours.getInterval())
                .build();
    }
}
