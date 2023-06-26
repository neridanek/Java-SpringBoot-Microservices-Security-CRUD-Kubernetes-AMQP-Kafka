package com.example.demo.dictionary;

import com.example.demo.ShapeApplication;
import com.example.demo.dictionary.command.CreateDictionaryCommand;
import com.example.demo.dictionary.command.CreateValuesForDictionaryCommand;
import com.example.demo.dictionary.command.EditValueInDictionaryCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShapeApplication.class)
@AutoConfigureMockMvc
class DictionaryControllerTest {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;


    @Test
    void saveDictionary() throws Exception {
        Set<String> testValues = Set.of("test value 01", "test value 02");
        CreateDictionaryCommand testCommand = CreateDictionaryCommand.builder()
                .name("test dictionary 02")
                .launchValues(testValues)
                .build();

        String json = objectMapper.writeValueAsString(testCommand);

        String responseJson = postman.perform(post("/api/v1/dictionaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test dictionary 02"))
                .andExpect(jsonPath("$.values", hasSize(2)))
                .andExpect(jsonPath("$.values[0]").isNotEmpty())
                .andExpect(jsonPath("$.values[1]").isNotEmpty())
                .andExpect(jsonPath("$.deleted").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        DictionaryDto saved = objectMapper.readValue(responseJson, DictionaryDto.class);

        Dictionary recentlyAdded = dictionaryRepository.findByIdWithValues(saved.id()).get();

        Assertions.assertEquals("test dictionary 02", recentlyAdded.getName());
        Assertions.assertFalse(recentlyAdded.isDeleted());

        Set<String> values = new HashSet<>();
        for (DictionaryValue dictionaryValue : recentlyAdded.getValues()) {
            values.add(dictionaryValue.getValue());
        }

        Assertions.assertTrue(values.contains("test value 01"));
        Assertions.assertTrue(values.contains("test value 02"));
    }

    @Test
    void getDictionary() throws Exception {
        int id = dictionaryRepository.saveAndFlush(new Dictionary("test dictionary 01")).getId();

        postman.perform(get("/api/v1/dictionaries/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("test dictionary 01"))
                .andExpect(jsonPath("$.values").isEmpty())
                .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    void saveValuesForDictionary() throws Exception {
        int id = dictionaryRepository.saveAndFlush(new Dictionary("test dictionary 03")).getId();
        Set<String> testValues = Set.of("test value 03", "test value 04");
        CreateValuesForDictionaryCommand testCommand = CreateValuesForDictionaryCommand.builder()
                .dictionaryId(id)
                .values(testValues)
                .build();

        String json = objectMapper.writeValueAsString(testCommand);

        String responseJson = postman.perform(post("/api/v1/dictionaries/" + id + "/values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test dictionary 03"))
                .andExpect(jsonPath("$.values", hasSize(2)))
                .andExpect(jsonPath("$.values[0]").isNotEmpty())
                .andExpect(jsonPath("$.values[1]").isNotEmpty())
                .andExpect(jsonPath("$.deleted").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        DictionaryDto saved = objectMapper.readValue(responseJson, DictionaryDto.class);

        Dictionary withNewValues = dictionaryRepository.findByIdWithValues(saved.id()).get();

        Assertions.assertEquals("test dictionary 03", withNewValues.getName());
        Assertions.assertFalse(withNewValues.isDeleted());

        Set<String> recentlyAddedValues = new HashSet<>();
        for (DictionaryValue dictionaryValue : withNewValues.getValues()) {
            recentlyAddedValues.add(dictionaryValue.getValue());
        }

        Assertions.assertTrue(recentlyAddedValues.contains("test value 03"));
        Assertions.assertTrue(recentlyAddedValues.contains("test value 04"));
    }

    @Test
    void editValueForDictionary() throws Exception {
        Dictionary testDictionary04 = new Dictionary("test dictionary 04");
        Set<DictionaryValue> testValues = Set.of(
                new DictionaryValue("test value to change", testDictionary04),
                new DictionaryValue("test value not to change", testDictionary04)
        );
        testDictionary04.setValues(testValues);
        int dictionaryId = dictionaryRepository.saveAndFlush(testDictionary04).getId();
        int valueId = dictionaryValueRepository.findByDictionaryNameAndValue(testDictionary04.getName(), "test value to change").get().getId();
        EditValueInDictionaryCommand testCommand = EditValueInDictionaryCommand.builder()
                .dictionaryId(dictionaryId)
                .newValue("changed test value")
                .valueId(valueId)
                .build();

        String json = objectMapper.writeValueAsString(testCommand);

        String responseJson = postman.perform(put("/api/v1/dictionaries/" + dictionaryId + "/values/" + valueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("test dictionary 04"))
                .andExpect(jsonPath("$.values", hasSize(2)))
                .andExpect(jsonPath("$.values[0]").isNotEmpty())
                .andExpect(jsonPath("$.values[1]").isNotEmpty())
                .andExpect(jsonPath("$.deleted").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();

        DictionaryDto saved = objectMapper.readValue(responseJson, DictionaryDto.class);

        Dictionary edited = dictionaryRepository.findByIdWithValues(saved.id()).get();

        Assertions.assertEquals("test dictionary 04", edited.getName());
        Assertions.assertFalse(edited.isDeleted());

        Set<String> editedValues = new HashSet<>();
        for (DictionaryValue dictionaryValue : edited.getValues()) {
            editedValues.add(dictionaryValue.getValue());
        }

        Assertions.assertFalse(editedValues.contains("test value to change"));
        Assertions.assertTrue(editedValues.contains("changed test value"));
        Assertions.assertTrue(editedValues.contains("test value not to change"));
    }

    @Test
    void deleteDictionaryAndItAllValues() throws Exception {
        int id = dictionaryRepository.saveAndFlush(new Dictionary("to be deleted")).getId();
        Set<String> testValues = Set.of("te be deleted 1", "te be deleted 2");
        CreateValuesForDictionaryCommand testCommand = CreateValuesForDictionaryCommand.builder()
                .dictionaryId(id)
                .values(testValues)
                .build();

        String json = objectMapper.writeValueAsString(testCommand);

        postman.perform(post("/api/v1/dictionaries/" + id + "/values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        Dictionary beforeDeleted = dictionaryRepository.findByIdWithValues(id).get();

        postman.perform(delete("/api/v1/dictionaries/" + id))
                .andExpect(status().isNoContent());


        Assertions.assertFalse(dictionaryRepository.existsById(id));
        beforeDeleted.getValues().stream().forEach(value -> Assertions.assertFalse(dictionaryValueRepository.existsById(value.getId())));
    }

}