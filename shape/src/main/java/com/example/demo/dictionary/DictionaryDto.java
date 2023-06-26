package com.example.demo.dictionary;

import java.util.Set;
import java.util.stream.Collectors;

public record DictionaryDto(int id, String name, Set<String> values, boolean deleted) {
    public static DictionaryDto fromEntity(Dictionary entity) {
        return new DictionaryDto(entity.getId(),
                entity.getName(),
                entity.getValues().stream().map(DictionaryValue::getValue).collect(Collectors.toSet()),
                entity.isDeleted());
    }
}
