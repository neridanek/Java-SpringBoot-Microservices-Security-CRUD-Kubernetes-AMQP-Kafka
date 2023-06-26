package com.example.demo.dictionary;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "name")
@Where(clause = "deleted = false")
//@SQLDelete(sql="update dictionary set deleted = true where id = ?1")
public class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dictionaryIdGenerator")
    @SequenceGenerator(name = "dictionaryIdGenerator", sequenceName = "dictionary_seq", initialValue = 1000, allocationSize = 100)
    private int id;
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "dictionary", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<DictionaryValue> values = new HashSet<>();
    @Column(name = "deleted")
    private boolean deleted;

    public Dictionary(String name) {
        this.name = name;
    }

    public Dictionary(String name, Set<String> values) {
        this.name = name;
        this.values = values.stream().map(v -> new DictionaryValue(v, this)).collect(Collectors.toSet());
    }
    public void addNewValues(Set<String> values) {
        this.values.addAll(values.stream().map(v -> new DictionaryValue(v, this)).collect(Collectors.toSet()));
    }

}