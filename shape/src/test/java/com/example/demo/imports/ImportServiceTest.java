package com.example.demo.imports;

import com.example.demo.ShapeApplication;
import com.example.demo.shape.Shape;
import com.example.demo.shape.ShapeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = ShapeApplication.class)
@ActiveProfiles("tests")
class ImportServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ImportStatusRepository importStatusRepository;

    @Mock
    private ShapeRepository bookRepository;

    @InjectMocks
    private ImportService importService;

    @Test
    void startImport() {
        String fileName = "test.csv";
        ImportStatus savedImportStatus = new ImportStatus(fileName);

        when(importStatusRepository.saveAndFlush(any(ImportStatus.class))).thenReturn(savedImportStatus);

        ImportStatus importStatus = importService.startImport(fileName);

        assertEquals(importStatus.getFileName(),fileName);

    }

    @Test
    void findById() {
        String fileName = "test.csv";
        ImportStatus savedImportStatus = new ImportStatus(fileName);
        int id = 1;

        importStatusRepository.saveAndFlush(savedImportStatus);

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(savedImportStatus));

        ImportStatus result = importService.findById(id);

        assertEquals(savedImportStatus,result);

    }

    @Test
    void importBook() throws ExecutionException, InterruptedException {
        String csvData = "title_0,ROMANCE,2";
        String sqlQuery = "insert into book (title, category, available, author_id) values (?,?,?,?)";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        int id = 1;

        ImportStatus importStatus = new ImportStatus("test.csv");
        importStatus.setId(id);

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));


        CompletableFuture<Void> importProcess = CompletableFuture.runAsync(() -> {
            importService.importShape(inputStream, id);
        });

        importProcess.get();

        verify(importStatusRepository, times(2)).findById(id);
        verify(importStatusRepository, times(2)).saveAndFlush(any(ImportStatus.class));

        verify(jdbcTemplate, times(1)).update(eq(sqlQuery), eq("title_0"), eq("ROMANCE"), eq(true), eq(2));

        assertEquals(importStatus.getStatus(), ImportStatus.Status.SUCCESS);

    }

    @Test
    void importBook_statusFailed() throws ExecutionException, InterruptedException, TimeoutException {
        String csvData = "title_0,ROMANCE,asd";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        int id = 2;

        ImportStatus importStatus = new ImportStatus("test.csv");
        importStatus.setId(id);

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        doAnswer(invocation -> {
            throw new Exception();
        }).when(bookRepository).save(any(Shape.class));

        CompletableFuture<Void> importProcess = CompletableFuture.runAsync(() -> {
            importService.importShape(inputStream, id);
        });

        importProcess.get();

        verify(importStatusRepository, times(2)).findById(id);
        verify(importStatusRepository,times(1)).saveAndFlush(any(ImportStatus.class));
        assertEquals(importStatus.getStatus(), ImportStatus.Status.FAILED);
    }

    @Test
    void save() {
        String csvData = "title_0,ROMANCE,2";
        String[] args = csvData.split(",");
        String sqlQuery = "insert into book (title, category, available, author_id) values (?,?,?,?)";

        importService.save(args);

        verify(jdbcTemplate,times(1)).update(
                eq(sqlQuery),eq(args[0]),eq(args[1]),eq(true),eq(Integer.parseInt(args[2]))
        );
    }

    @Test
    void updateToSuccess() {
        int id = 1;
        ImportStatus importStatus = new ImportStatus("test.csv");

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));
        when(importStatusRepository.saveAndFlush(any(ImportStatus.class))).thenReturn(importStatus);

        importService.updateToSuccess(id);

        verify(importStatusRepository, times(1)).findById(id);
        verify(importStatusRepository, times(1)).saveAndFlush(importStatus);

        assertEquals(ImportStatus.Status.SUCCESS, importStatus.getStatus());
        assertNotNull(importStatus.getFinishDate());
    }

    @Test
    void updateToProcessing() {
        int id = 1;
        ImportStatus importStatus = new ImportStatus("test.csv");

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));
        when(importStatusRepository.saveAndFlush(any(ImportStatus.class))).thenReturn(importStatus);

        importService.updateToProcessing(id);

        verify(importStatusRepository, times(1)).findById(id);
        verify(importStatusRepository, times(1)).saveAndFlush(importStatus);

        assertEquals(ImportStatus.Status.PROCESSING, importStatus.getStatus());
        assertNotNull(importStatus.getStartDate());
    }

    @Test
    void countTime() {
        AtomicInteger counter = new AtomicInteger(9999);
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        int id = 1;

        ImportStatus importStatus = new ImportStatus();
        when(importStatusRepository.findById(eq(id))).thenReturn(Optional.of(importStatus)); // Mock findById to return the ImportStatus instance

        importService.countTime(counter, start, id);

        assertEquals(10000, counter.get());
        verify(importStatusRepository, times(1)).findById(id);
        verify(importStatusRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void updateProgress() {
        AtomicInteger counter = new AtomicInteger(0);
        int id = 1;
        ImportStatus importStatus =new ImportStatus();
        importStatus.setId(id);

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        importService.updateProgress(id,counter.incrementAndGet());

        verify(importStatusRepository).findById(id);
        verify(importStatusRepository,times(1)).saveAndFlush(importStatus);

        assertEquals(importStatus.getProcessed(),counter.intValue());
    }
    @Test
    void updateProgress_IdNotFound() {
        AtomicInteger counter = new AtomicInteger(0);
        int id = 1;

        assertThrows(
                NoSuchElementException.class,
                () -> importService.updateProgress(id, counter.incrementAndGet())
        );

        verify(importStatusRepository).findById(id);

        verify(importStatusRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateToFailed(){
        int id = 1;
        ImportStatus importStatus =new ImportStatus();
        importStatus.setId(id);

        Exception exception = new Exception("error message");

        when(importStatusRepository.findById(id)).thenReturn(Optional.of(importStatus));

        importService.updateToFailed(1,exception);

        verify(importStatusRepository,times(1)).findById(id);

        assertEquals(importStatus.getStatus(), ImportStatus.Status.FAILED);
        assertNotNull(importStatus.getFailedReason());
    }

}