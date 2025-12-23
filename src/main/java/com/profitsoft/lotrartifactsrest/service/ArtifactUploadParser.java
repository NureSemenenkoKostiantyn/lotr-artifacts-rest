package com.profitsoft.lotrartifactsrest.service;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.lotrartifactsrest.dto.ArtifactSaveDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class ArtifactUploadParser {

    private final JsonFactory jsonFactory = new JsonFactory();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImportResult parse(MultipartFile file, Consumer<ArtifactSaveDto> onArtifact) {
        validateFile(file);

        long importedCount = 0;
        long failedCount = 0;

        try (InputStream inputStream = file.getInputStream();
             JsonParser parser = jsonFactory.createParser(inputStream)) {

            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalArgumentException("JSON must start with an array of artifact objects");
            }

            while (parser.nextToken() == JsonToken.START_OBJECT) {
                try {
                    JsonNode node = objectMapper.readTree(parser);
                    ArtifactSaveDto dto = objectMapper.treeToValue(node, ArtifactSaveDto.class);
                    onArtifact.accept(dto);
                    importedCount++;
                } catch (Exception ex) {
                    failedCount++;
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process the uploaded file", e);
        }

        return new ImportResult(importedCount, failedCount);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (Objects.nonNull(contentType) && !MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            throw new IllegalArgumentException("File must be of JSON type");
        }
    }

    public record ImportResult(long imported, long failed) {}
}

