package com.example.demo.dictionary.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class EditValueInDictionaryCommand {
    private int dictionaryId;
    private int valueId;
    private String newValue;
}