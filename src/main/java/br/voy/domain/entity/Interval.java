package br.voy.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Schema(name = "Interval", description = "Open and close times for a business day")
public class Interval {

    @Schema(description = "Opening time", example = "09:00")
    private String start;

    @Schema(description = "Closing time", example = "18:00")
    private String end;
}
