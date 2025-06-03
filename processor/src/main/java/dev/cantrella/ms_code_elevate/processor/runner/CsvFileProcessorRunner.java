package dev.cantrella.ms_code_elevate.processor.runner;

import dev.cantrella.ms_code_elevate.processor.process.BookProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CsvFileProcessorRunner implements ApplicationRunner {

    @Autowired
    private BookProcess bookProcess;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bookProcess.processCsvFile("books_df.csv");
    }
}