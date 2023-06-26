package com.example.demo.imports;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ImportStatus {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private LocalDateTime submitDate;
    private LocalDateTime startDate;
    private Status status;
    private LocalDateTime finishDate;
    private String failedReason;
    private int processed;
    private String fileName;

    public ImportStatus(String fileName) {
        this.fileName = fileName;
        this.submitDate = LocalDateTime.now();
        this.status = Status.NEW;
    }

    public static enum Status {
        NEW, PROCESSING, SUCCESS, FAILED
    }
}