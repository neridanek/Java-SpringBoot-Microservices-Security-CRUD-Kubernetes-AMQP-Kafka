package com.example.demo.dictionary;

import com.example.demo.dictionary.command.CreateDictionaryCommand;
import com.example.demo.dictionary.command.CreateValuesForDictionaryCommand;
import com.example.demo.dictionary.command.EditValueInDictionaryCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dictionaries")
@Slf4j
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @PostMapping
    public ResponseEntity<DictionaryDto> saveDictionary(@RequestBody CreateDictionaryCommand command) {
        log.info("saveDictionary({})", command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dictionaryService.save(command));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DictionaryDto> getDictionary(@PathVariable int id) {
        log.info("findDictionary({})", id);
        return ResponseEntity.ok(DictionaryDto.fromEntity(dictionaryService.findById(id).orElseThrow()));
    }

    @PostMapping("/{id}/values")
    public ResponseEntity saveValuesForDictionary(@PathVariable int id, @RequestBody CreateValuesForDictionaryCommand command) {
        log.info("saveValueForDictionary({},{})", id, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dictionaryService.addValues(command));
    }

    @PutMapping("/{id}/values/{valueId}")
    public ResponseEntity<DictionaryDto> editValueForDictionary(@PathVariable int id, @RequestBody EditValueInDictionaryCommand command) {
        log.info("editValueInDictionary({},{})", id, command);
        return ResponseEntity.status(HttpStatus.OK).body(dictionaryService.editValue(command));
    }

    //**
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDictionary(@PathVariable int id) {
        log.info("deleteDictionary({})", id);
        dictionaryService.deleteDictionary(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{dictionaryId}/values/{valueId}")
    public ResponseEntity deleteValueFromDictionary(@PathVariable int dictionaryId, @PathVariable int valueId) {
        log.info("deleteValueFromDictionary({},{})", dictionaryId, valueId);
        dictionaryService.deleteValueFromDictionary(dictionaryId, valueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<Page<DictionaryValueDto>> findAll(@PageableDefault Pageable pageable) {
        log.info("findAll({})", pageable);
        return ResponseEntity.ok(dictionaryService.findAll(pageable).map(DictionaryValueDto::fromEntity));
    }
}
