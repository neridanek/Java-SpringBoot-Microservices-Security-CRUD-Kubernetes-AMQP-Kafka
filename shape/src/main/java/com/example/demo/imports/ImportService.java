package com.example.demo.imports;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportService {

    private static final String INSERT_SHAPE_SQL = "insert into shapes (type) values (?)";
    //TODO: Fix shape parameters in db schema
    private final JdbcTemplate jdbcTemplate;
    private final ImportStatusRepository importStatusRepository;

    public ImportStatus startImport(String fileName) {
        return importStatusRepository.saveAndFlush(new ImportStatus(fileName));
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    public ImportStatus findById(int id) {
        return importStatusRepository.findById(id).orElseThrow();
    }

    @Transactional
    @Async("shapeImportExecutor")
    public void importShape(InputStream inputStream,int id){

        updateToProcessing(id);

        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong start = new AtomicLong(System.currentTimeMillis());

        try{
            new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .map(line->line.split(","))
                    .peek(shapeRequest->countTime(counter,start,id))
                    .forEach(this::save);
        }catch(Exception e){
            log.error("Exception during import", e);
            updateToFailed(id,e);
            throw new IllegalStateException(e);
        }

        updateToSuccess(id);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFailed(int id, Exception e){
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow();
        toUpdate.setFailedReason(e.getMessage());
        toUpdate.setStatus(ImportStatus.Status.FAILED);
        toUpdate.setFinishDate(LocalDateTime.now());
        importStatusRepository.saveAndFlush(toUpdate);
    }

    protected void updateToSuccess(int id) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow();
        toUpdate.setFinishDate(LocalDateTime.now());
        toUpdate.setStatus(ImportStatus.Status.SUCCESS);
        importStatusRepository.saveAndFlush(toUpdate);
    }

    protected void updateToProcessing(int id) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow();
        toUpdate.setStartDate(LocalDateTime.now());
        toUpdate.setStatus(ImportStatus.Status.PROCESSING);
        importStatusRepository.saveAndFlush(toUpdate);
    }

    public void save(String[] args) {
        jdbcTemplate.update(INSERT_SHAPE_SQL,args[0]);
    }

    protected void countTime(AtomicInteger counter, AtomicLong start, int id) {
        int progress = counter.incrementAndGet();
        if (progress % 10000 == 0) {
            log.info("Imported: {} in {} ms", progress, (System.currentTimeMillis() - start.get()));
            start.set(System.currentTimeMillis());
            updateProgress(id, progress);
        }
    }

    protected void updateProgress(int id, int progress) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow();
        toUpdate.setProcessed(progress);
        importStatusRepository.saveAndFlush(toUpdate);
    }


}
