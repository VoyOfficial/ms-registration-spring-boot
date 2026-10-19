package br.voy.application.controller.response;

import br.voy.domain.entity.BusinessHours;
import br.voy.domain.entity.Interval;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "BusinessHoursResponse", description = "Opening hours for one weekday")
public class BusinessHoursResponse {

    @Schema(description = "Weekday label", example = "Monday")
    private String day;

    @Schema(description = "Open and close interval for the day")
    private Interval interval;

    public static BusinessHoursResponse toBusinessHoursResponse(BusinessHours businessHours) {

        return BusinessHoursResponse.builder()
                .day(businessHours.getDay())
                .interval(businessHours.getInterval())
                .build();
    }
}
