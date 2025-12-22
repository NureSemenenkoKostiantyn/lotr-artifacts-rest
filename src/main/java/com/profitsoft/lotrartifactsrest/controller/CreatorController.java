package com.profitsoft.lotrartifactsrest.controller;

import com.profitsoft.lotrartifactsrest.dto.CreatorDetailsDto;
import com.profitsoft.lotrartifactsrest.dto.CreatorSaveDto;
import com.profitsoft.lotrartifactsrest.service.CreatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
@Tag(name = "Creators", description = "API for managing creators of artifacts")
public class CreatorController {
    public final CreatorService creatorService;

    @Operation(summary = "Get all creator details", description = "Retrieves all creators details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creators found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
    })
    @GetMapping()
    public List<CreatorDetailsDto> getCreators() {
        return creatorService.getAllCreators();
    }

    @Operation(summary = "Save creator entity", description = "Saves creator entity to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creator created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorDetailsDto saveCreator(@Valid @Parameter(description = "Creator data", required = true) @RequestBody CreatorSaveDto creatorSaveDto) {
        return creatorService.saveCreator(creatorSaveDto);
    }

    @Operation(summary = "Get creator by ID", description = "Retrieves creator details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creator found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Creator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{creatorId}")
    public CreatorDetailsDto getCreatorById(@Parameter(description = "ID of the creator to retrieve", required = true) @PathVariable Long creatorId) {
        return creatorService.getCreatorById(creatorId);
    }

    @Operation(summary = "Update creator by ID", description = "Updates creator details by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creator updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Creator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{creatorId}")
    public CreatorDetailsDto updateCreator(@Parameter(description = "ID of the creator to update", required = true) @PathVariable Long creatorId,
                                           @Valid @Parameter(description = "Updated creator data", required = true) @RequestBody CreatorSaveDto creatorSaveDto) {
        return creatorService.updateCreator(creatorId, creatorSaveDto);
    }

    @Operation(summary = "Delete creator by ID", description = "Deletes creator by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Creator deleted"),
            @ApiResponse(responseCode = "404", description = "Creator not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{creatorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCreator(@Parameter(description = "ID of the creator to delete", required = true) @PathVariable Long creatorId) {
        creatorService.deleteCreator(creatorId);
    }
}
