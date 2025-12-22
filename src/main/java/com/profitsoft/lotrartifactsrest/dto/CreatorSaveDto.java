package com.profitsoft.lotrartifactsrest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for creator saving.
 */
@Getter
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Creator save data transfer object")
public class CreatorSaveDto {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Race must not be null")
    @NotBlank(message = "Race must not be blank")
    private String race;

    @NotNull(message = "Realm must not be null")
    @NotBlank(message = "Realm must not be blank")
    private String realm;
}
