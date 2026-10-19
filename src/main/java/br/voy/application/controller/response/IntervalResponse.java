package br.voy.application.controller.response;

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
@Schema(name = "IntervalResponse", description = "Open and close times for a business day")
public class IntervalResponse {

    @Schema(description = "Opening time", example = "09:00")
    private String start;

    @Schema(description = "Closing time", example = "18:00")
    private String end;

    public static IntervalResponse fromDomain(Interval interval) {
        if (interval == null) {
            return null;
        }
        return IntervalResponse.builder().start(interval.getStart()).end(interval.getEnd()).build();
    }
}
