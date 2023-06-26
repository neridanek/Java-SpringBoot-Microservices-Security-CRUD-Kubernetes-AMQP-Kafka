package com.example.demo.dictionary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"value", "dictionary"})
@Where(clause = "deleted = false")
//@SQLDelete(sql="update dictionary_value set deleted = true where id = ?1")
public class DictionaryValue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dictionaryValueIdGenerator")
    @SequenceGenerator(name = "dictionaryValueIdGenerator", sequenceName = "dict_val_seq", initialValue = 100000, allocationSize = 100)
    private int id;
    @Column(name = "dict_value")
    private String value;
    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;
    @Column(name = "deleted")
    private boolean deleted;

    public DictionaryValue(String value, Dictionary dictionary) {
        this.value = value;
        this.dictionary = dictionary;
        this.dictionary.getValues().add(this);
    }
}
