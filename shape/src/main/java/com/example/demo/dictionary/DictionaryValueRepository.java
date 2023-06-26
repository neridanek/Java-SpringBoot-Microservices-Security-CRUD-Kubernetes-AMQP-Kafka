package com.example.demo.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DictionaryValueRepository extends JpaRepository<DictionaryValue, Integer> {
    Optional<DictionaryValue> findByDictionaryNameAndValue(String countries, String country);
    @Modifying
    @Query("update DictionaryValue dv set dv.deleted=true where dv.dictionary.id = ?1")
    void deleteByDictionary_id(int dictionaryId);
}
