package com.profitsoft.lotrartifactsrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profitsoft.lotrartifactsrest.dto.CreatorSaveDto;
import com.profitsoft.lotrartifactsrest.model.Creator;
import com.profitsoft.lotrartifactsrest.repository.CreatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CreatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreatorRepository creatorRepository;

    @BeforeEach
    void setUp() {
        creatorRepository.deleteAll();
    }

    @Test
    void shouldCreateCreator() throws Exception {
        CreatorSaveDto request = CreatorSaveDto.builder()
                .name("Gandalf")
                .race("Maia")
                .realm("Middle-earth")
                .build();

        mockMvc.perform(post("/api/creators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Gandalf")))
                .andExpect(jsonPath("$.race", is("Maia")))
                .andExpect(jsonPath("$.realm", is("Middle-earth")));
    }

    @Test
    void shouldRejectDuplicateCreatorNameOnCreate() throws Exception {
        Creator existing = new Creator();
        existing.setName("Elrond");
        existing.setRace("Elf");
        existing.setRealm("Rivendell");
        creatorRepository.save(existing);

        CreatorSaveDto request = CreatorSaveDto.builder()
                .name("Elrond")
                .race("Elf")
                .realm("Rivendell")
                .build();

        mockMvc.perform(post("/api/creators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetCreatorById() throws Exception {
        Creator creator = new Creator();
        creator.setName("Thror");
        creator.setRace("Dwarf");
        creator.setRealm("Erebor");
        creator = creatorRepository.save(creator);

        mockMvc.perform(get("/api/creators/{id}", creator.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(creator.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Thror")))
                .andExpect(jsonPath("$.race", is("Dwarf")))
                .andExpect(jsonPath("$.realm", is("Erebor")));
    }

    @Test
    void shouldUpdateCreator() throws Exception {
        Creator creator = new Creator();
        creator.setName("Saruman");
        creator.setRace("Maia");
        creator.setRealm("Isengard");
        creator = creatorRepository.save(creator);

        CreatorSaveDto updateRequest = CreatorSaveDto.builder()
                .name("Saruman the White")
                .race("Maia")
                .realm("Orthanc")
                .build();

        mockMvc.perform(put("/api/creators/{id}", creator.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Saruman the White")))
                .andExpect(jsonPath("$.realm", is("Orthanc")));
    }

    @Test
    void shouldEnforceUniqueNameOnUpdate() throws Exception {
        Creator first = new Creator();
        first.setName("Thingol");
        first.setRace("Elf");
        first.setRealm("Doriath");
        creatorRepository.save(first);

        Creator second = new Creator();
        second.setName("Finwe");
        second.setRace("Elf");
        second.setRealm("Valinor");
        second = creatorRepository.save(second);

        CreatorSaveDto updateRequest = CreatorSaveDto.builder()
                .name("Thingol")
                .race("Elf")
                .realm("Valinor")
                .build();

        mockMvc.perform(put("/api/creators/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldDeleteCreator() throws Exception {
        Creator creator = new Creator();
        creator.setName("Durin");
        creator.setRace("Dwarf");
        creator.setRealm("Khazad-dum");
        creator = creatorRepository.save(creator);

        mockMvc.perform(delete("/api/creators/{id}", creator.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/creators/{id}", creator.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllCreators() throws Exception {
        Creator first = new Creator();
        first.setName("Cirdan");
        first.setRace("Elf");
        first.setRealm("Havens");
        Creator second = new Creator();
        second.setName("Beren");
        second.setRace("Man");
        second.setRealm("Dorthonion");
        creatorRepository.save(first);
        creatorRepository.save(second);

        mockMvc.perform(get("/api/creators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cirdan", "Beren")));
    }
}
