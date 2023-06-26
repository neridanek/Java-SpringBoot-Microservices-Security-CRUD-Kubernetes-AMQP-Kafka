package com.example.demo.dictionary;

import com.example.demo.dictionary.command.CreateDictionaryCommand;
import com.example.demo.dictionary.command.CreateValuesForDictionaryCommand;
import com.example.demo.dictionary.command.EditValueInDictionaryCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryValueRepository dictionaryValueRepository;

    @Transactional
    public DictionaryDto save(CreateDictionaryCommand command) {
        Dictionary savedDictionary = dictionaryRepository.save(new Dictionary(command.getName(), command.getLaunchValues()));
        return DictionaryDto.fromEntity(savedDictionary);
    }

    @Transactional(readOnly = true)
    public Optional<Dictionary> findById(int id) {
        return dictionaryRepository.findByIdWithValues(id);
    }

    @Transactional
    public DictionaryDto addValues(CreateValuesForDictionaryCommand command) {
        Dictionary dictionary = dictionaryRepository.findByIdWithValues(command.getDictionaryId()).orElseThrow();
        dictionary.addNewValues(command.getValues());
        return DictionaryDto.fromEntity(dictionaryRepository.saveAndFlush(dictionary));
    }

    @Transactional
    public DictionaryDto editValue(EditValueInDictionaryCommand command) {
        DictionaryValue dictionaryValue =
                dictionaryValueRepository.findById(command.getValueId()).orElseThrow();
        dictionaryValue.setValue(command.getNewValue());
        dictionaryValueRepository.save(dictionaryValue);
        return DictionaryDto.fromEntity(dictionaryValue.getDictionary());
    }

    @Transactional
    public void deleteDictionary(int dictionaryId) {
        dictionaryValueRepository.deleteByDictionary_id(dictionaryId);
        dictionaryRepository.deleteByIdWithSoftDelete(dictionaryId);
    }

    @Transactional
    public void deleteValueFromDictionary(int dictionaryId, int valueId) {
        dictionaryValueRepository.deleteById(valueId);
    }

    public Page<DictionaryValue> findAll(Pageable pageable) {
        return dictionaryValueRepository.findAll(pageable);
    }

}