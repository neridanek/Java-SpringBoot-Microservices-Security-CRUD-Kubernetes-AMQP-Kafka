package com.example.demo.dictionary;

public record DictionaryValueDto(int id, String value, int dictionaryId, boolean deleted) {
    public static DictionaryValueDto fromEntity(DictionaryValue entity) {
        return new DictionaryValueDto(entity.getId(),
                entity.getValue(),
                entity.getDictionary().getId(),
                entity.isDeleted());
    }
}
