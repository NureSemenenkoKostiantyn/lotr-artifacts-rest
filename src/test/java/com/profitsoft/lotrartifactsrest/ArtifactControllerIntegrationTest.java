package com.profitsoft.lotrartifactsrest;

import com.profitsoft.lotrartifactsrest.dto.ArtifactListRequestDto;
import com.profitsoft.lotrartifactsrest.dto.ArtifactSaveDto;
import com.profitsoft.lotrartifactsrest.model.Artifact;
import com.profitsoft.lotrartifactsrest.model.Creator;
import com.profitsoft.lotrartifactsrest.repository.ArtifactRepository;
import com.profitsoft.lotrartifactsrest.repository.CreatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ArtifactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private CreatorRepository creatorRepository;



    @BeforeEach
    void setUp() {
        artifactRepository.deleteAll();
        creatorRepository.deleteAll();
    }

    @Test
    void shouldCreateArtifact() throws Exception {
        Creator creator = createCreator("Sauron", "Maia", "Mordor");
        ArtifactSaveDto request = ArtifactSaveDto.builder()
                .name("Palantir")
                .creatorId(creator.getId())
                .origin("Gondor")
                .tags("seeing-stone")
                .yearCreated(1500)
                .powerLevel(5000)
                .build();

        mockMvc.perform(post("/api/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Palantir")))
                .andExpect(jsonPath("$.creator.name", is("Sauron")));
    }

    @Test
    void shouldGetArtifactById() throws Exception {
        Creator creator = createCreator("Frodo", "Hobbit", "Shire");
        Artifact artifact = createArtifact("Sting", creator, "Shire", "sword", 2968, 1200);

        mockMvc.perform(get("/api/artifact/{id}", artifact.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sting")))
                .andExpect(jsonPath("$.creator.id", is(creator.getId().intValue())))
                .andExpect(jsonPath("$.origin", is("Shire")));
    }

    @Test
    void shouldUpdateArtifact() throws Exception {
        Creator creator = createCreator("Feanor", "Elf", "Valinor");
        Artifact artifact = createArtifact("Silmaril", creator, "Valinor", "jewel", 1450, 9000);
        Creator newCreator = createCreator("Aule", "Valar", "Valinor");

        ArtifactSaveDto updateRequest = ArtifactSaveDto.builder()
                .name("Silmaril of Earendil")
                .creatorId(newCreator.getId())
                .origin("Valinor")
                .tags("jewel,light")
                .yearCreated(1450)
                .powerLevel(9500)
                .build();

        mockMvc.perform(put("/api/artifact/{id}", artifact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Silmaril of Earendil")))
                .andExpect(jsonPath("$.creator.name", is("Aule")))
                .andExpect(jsonPath("$.powerLevel", is(9500)));
    }

    @Test
    void shouldDeleteArtifact() throws Exception {
        Creator creator = createCreator("Sauron", "Maia", "Mordor");
        Artifact artifact = createArtifact("Morgul Blade", creator, "Mordor", "dagger", 3420, 7000);

        mockMvc.perform(delete("/api/artifact/{id}", artifact.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/artifact/{id}", artifact.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListArtifactsWithPagination() throws Exception {
        Creator creator = createCreator("Celebrimbor", "Elf", "Eregion");
        createArtifact("Ring of Fire", creator, "Eregion", "ring", 1590, 8500);
        createArtifact("Ring of Water", creator, "Eregion", "ring", 1590, 8500);
        createArtifact("Ring of Air", creator, "Eregion", "ring", 1590, 8500);

        ArtifactListRequestDto request = new ArtifactListRequestDto();
        request.setCreatorId(creator.getId());
        request.setPage(0);
        request.setSize(2);

        mockMvc.perform(post("/api/artifact/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list", hasSize(2)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }

    @Test
    void shouldGenerateReport() throws Exception {
        Creator creator = createCreator("Gandalf", "Maia", "Middle-earth");
        createArtifact("Glamdring", creator, "Gondolin", "sword", 1000, 4000);

        ArtifactListRequestDto request = new ArtifactListRequestDto();
        request.setPage(0);
        request.setSize(10);

        mockMvc.perform(post("/api/artifact/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("artifacts-report.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(containsString("Glamdring")));
    }

    @Test
    void shouldUploadArtifacts() throws Exception {
        Creator sauron = createCreator("Sauron", "Maia", "Mordor");
        Creator feanor = createCreator("Feanor", "Elf", "Valinor");

        String payload = Files.readString(Path.of("src/main/resources/artifacts-upload.json"))
                .replace("\"creatorId\": 1", "\"creatorId\": " + sauron.getId())
                .replace("\"creatorId\": 3", "\"creatorId\": " + feanor.getId());
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "artifacts-upload.json",
                MediaType.APPLICATION_JSON_VALUE,
                payload.getBytes()
        );

        mockMvc.perform(multipart("/api/artifact/upload")
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported", is(15)))
                .andExpect(jsonPath("$.failed", is(15)));
    }

    private Creator createCreator(String name, String race, String realm) {
        Creator creator = new Creator();
        creator.setName(name);
        creator.setRace(race);
        creator.setRealm(realm);
        return creatorRepository.save(creator);
    }

    private Artifact createArtifact(String name, Creator creator, String origin, String tags, int year, int power) {
        Artifact artifact = new Artifact();
        artifact.setName(name);
        artifact.setCreator(creator);
        artifact.setOrigin(origin);
        artifact.setTags(tags);
        artifact.setYearCreated(year);
        artifact.setPowerLevel(power);
        return artifactRepository.save(artifact);
    }
}