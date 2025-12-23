package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(name = "ArtifactListRequest")
public class ArtifactListRequestDto {

    @Schema(description = "Filter by creator id", example = "2")
    private Long creatorId;

    @Schema(description = "Optional filter: origin", example = "Mordor")
    private String origin;

    @Schema(description = "Min year created", example = "1000")
    private Integer yearFrom;

    @Schema(description = "Max year created", example = "3000")
    private Integer yearTo;

    @Schema(description = "Min power level", example = "100")
    private Integer powerFrom;

    @Schema(description = "Max power level", example = "5000")
    private Integer powerTo;

    @Schema(description = "Page (0-based)", example = "0")
    @Min(0)
    private Integer page = 0;

    @Schema(description = "Size", example = "20")
    @Min(1)
    private Integer size = 20;
}