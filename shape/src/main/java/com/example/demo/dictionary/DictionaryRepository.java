package com.example.demo.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<Dictionary, Integer> {

    @Query("select d from Dictionary d left join fetch d.values where d.id = ?1")
    Optional<Dictionary> findByIdWithValues(int dictionaryId);

    @Modifying
    @Query("update Dictionary d set d.deleted=true where d.id = ?1")
    void deleteByIdWithSoftDelete(int dictionaryId);
}
