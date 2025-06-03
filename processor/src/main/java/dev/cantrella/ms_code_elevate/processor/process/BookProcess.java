package dev.cantrella.ms_code_elevate.processor.process;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import dev.cantrella.ms_code_elevate.processor.entity.Book;
import dev.cantrella.ms_code_elevate.processor.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;

@Service
public class BookProcess {

    @Autowired
    private BookRepository repository;

    public void processCsvFile(String file) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);
                CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> records = csvReader.readAll();
            boolean isHeader = true;

            for (String[] fields : records) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (fields.length < 10) {
                    System.err.println("Invalid line: " + String.join(",", fields));
                    continue;
                }

                try{
                    Book bookRecord = new Book();
                    bookRecord.setTitle(fields[1].trim());
                    bookRecord.setAuthor(fields[2].trim());
                    bookRecord.setMainGenre(fields[3].trim());
                    bookRecord.setSubGenre(fields[4].trim());
                    bookRecord.setType(fields[5].trim());
                    bookRecord.setPrice(fields[6].trim());
                    bookRecord.setRating(parseDouble(fields[7]));
                    bookRecord.setNumberOfPeopleRated(parseDouble(fields[8]).intValue());
                    bookRecord.setUrls(fields[9].trim());

                    repository.save(bookRecord);
                } catch (Exception e) {
                    System.out.println("Error to save book: " + fields[1].trim());
                }

            }

            System.out.println("CSV file processed successfully.");
        } catch (IOException | CsvException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }

    private Double parseDouble(String value) {
        if (StringUtils.hasText(value)) {
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid double value: " + value);
            }
        }
        return null;
    }

    private Integer parseInteger(String value) {
        if (StringUtils.hasText(value)) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer value: " + value);
            }
        }
        return null;
    }
}