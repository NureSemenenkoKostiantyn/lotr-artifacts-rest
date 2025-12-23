package com.profitsoft.lotrartifactsrest.service;

import com.profitsoft.lotrartifactsrest.dto.*;
import com.profitsoft.lotrartifactsrest.model.Artifact;
import com.profitsoft.lotrartifactsrest.model.Creator;
import com.profitsoft.lotrartifactsrest.repository.ArtifactRepository;
import com.profitsoft.lotrartifactsrest.repository.CreatorRepository;
import com.profitsoft.lotrartifactsrest.repository.spec.ArtifactSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ArtifactService {
    private final ArtifactRepository artifactRepository;
    private final CreatorRepository creatorRepository;
    private final ArtifactUploadParser artifactUploadParser;

    public ArtifactService(ArtifactRepository artifactRepository,
                           CreatorRepository creatorRepository,
                           ArtifactUploadParser artifactUploadParser) {
        this.artifactRepository = artifactRepository;
        this.creatorRepository = creatorRepository;
        this.artifactUploadParser = artifactUploadParser;
    }

    public ArtifactDetailsDto saveArtifact(ArtifactSaveDto dto) {
        Creator creator = creatorRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(dto.getCreatorId())));
        Artifact artifact = convertToEntity(dto, creator);
        Artifact savedArtifact = artifactRepository.save(artifact);
        return convertToDetailsDto(savedArtifact);
    }

    public ArtifactDetailsDto getArtifactById(Long artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        return convertToDetailsDto(artifact);
    }

    public ArtifactListResponseDto getPageableArtifactsList(ArtifactListRequestDto requestDto) {

        Specification<Artifact> spec = buildSpecification(requestDto);

        PageRequest pageRequest = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize()
        );

        Page<Artifact> page = artifactRepository.findAll(spec, pageRequest);

        return new ArtifactListResponseDto(
                page.getContent().stream()
                        .map(ArtifactService::convertToDetailsDto)
                        .toList(),
                page.getTotalPages()
        );
    }

    public ArtifactDetailsDto updateArtifact(Long artifactId, ArtifactSaveDto dto) {
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        Creator creator = creatorRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Creator with id '%s' not found".formatted(dto.getCreatorId())));

        artifact.setName(dto.getName());
        artifact.setCreator(creator);
        artifact.setOrigin(dto.getOrigin());
        artifact.setTags(dto.getTags());
        artifact.setYearCreated(dto.getYearCreated());
        artifact.setPowerLevel(dto.getPowerLevel());

        Artifact updatedArtifact = artifactRepository.save(artifact);
        return convertToDetailsDto(updatedArtifact);
    }

    public void deleteArtifact(Long artifactId) {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact with id '%s' not found".formatted(artifactId)));
        artifactRepository.deleteById(artifactId);
    }

    public ArtifactImportResponseDto importArtifacts(MultipartFile file) {
        ArtifactUploadParser.ImportResult importResult = artifactUploadParser.parse(file, dto -> {
            try {
                saveArtifact(dto);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        });

        return ArtifactImportResponseDto.builder()
                .imported(importResult.imported())
                .failed(importResult.failed())
                .build();
    }

    public byte[] generateArtifactsReport(ArtifactListRequestDto requestDto) {
        Specification<Artifact> spec = buildSpecification(requestDto);

        List<Artifact> artifacts = artifactRepository.findAll(spec);

        return buildArtifactsCsv(artifacts).getBytes(StandardCharsets.UTF_8);
    }

    private String buildArtifactsCsv(List<Artifact> artifacts) {
        StringBuilder sb = new StringBuilder(artifacts.size() * 128);

        writeCsvRow(sb, "Id", "Name", "Origin", "Tags", "Year Created", "Power Level",
                "Creator Name", "Creator Race", "Creator Realm");

        for (Artifact a : artifacts) {
            Creator c = a.getCreator();
            writeCsvRow(sb,
                    a.getId(),
                    a.getName(),
                    a.getOrigin(),
                    a.getTags(),
                    a.getYearCreated(),
                    a.getPowerLevel(),
                    c != null ? c.getName() : null,
                    c != null ? c.getRace() : null,
                    c != null ? c.getRealm() : null
            );
        }

        return sb.toString();
    }

    private void writeCsvRow(StringBuilder sb, Object... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escapeCsv(values[i]));
        }
        sb.append('\n');
    }

    private String escapeCsv(Object value) {
        if (value == null) return "";
        String s = String.valueOf(value);

        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!mustQuote) return s;

        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private Specification<Artifact> buildSpecification(ArtifactListRequestDto requestDto) {
        return Specification
                .where(ArtifactSpecification.hasCreator(requestDto.getCreatorId()))
                .and(ArtifactSpecification.hasOrigin(requestDto.getOrigin()))
                .and(ArtifactSpecification.yearBetween(
                        requestDto.getYearFrom(),
                        requestDto.getYearTo()))
                .and(ArtifactSpecification.powerBetween(
                        requestDto.getPowerFrom(),
                        requestDto.getPowerTo()));
    }

    private static Artifact convertToEntity(ArtifactSaveDto dto, Creator creator) {
        Artifact artifact = new Artifact();
        artifact.setName(dto.getName());
        artifact.setCreator(creator);
        artifact.setOrigin(dto.getOrigin());
        artifact.setTags(dto.getTags());
        artifact.setYearCreated(dto.getYearCreated());
        artifact.setPowerLevel(dto.getPowerLevel());
        return artifact;
    }

    private static ArtifactDetailsDto convertToDetailsDto(Artifact artifact) {
        return ArtifactDetailsDto.builder()
                .id(artifact.getId())
                .name(artifact.getName())
                .origin(artifact.getOrigin())
                .tags(artifact.getTags())
                .yearCreated(artifact.getYearCreated())
                .powerLevel(artifact.getPowerLevel())
                .creator(CreatorDetailsDto.builder()
                        .id(artifact.getCreator().getId())
                        .name(artifact.getCreator().getName())
                        .race(artifact.getCreator().getRace())
                        .realm(artifact.getCreator().getRealm())
                        .build())
                .build();
    }

}
