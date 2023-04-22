package fileAnalyzerMain.controllers;

import fileAnalyzerMain.config.Constants;
import fileAnalyzerMain.services.LogsAnalyzer;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
public class MyController {
    private final LogsAnalyzer service;

    @GetMapping()
    public ResponseEntity<Object> getLogsCount(@RequestParam String searchString,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate
                                                       startDate, @RequestParam int numberOfDays) {
        return ResponseEntity.ok(service.countEntriesInZipFile(searchString,
                Constants.ARCHIVE_PATH.toFile(),
                startDate,
                numberOfDays));
    }
}
