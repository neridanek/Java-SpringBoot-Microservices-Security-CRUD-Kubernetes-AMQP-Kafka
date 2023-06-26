package com.example.demo.dictionary.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Builder
public class CreateValuesForDictionaryCommand {
    private int dictionaryId;
    private Set<String> values;
}

