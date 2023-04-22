package fileAnalyzerMain.services;

import fileAnalyzerMain.config.Constants;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

@Service
public class LogsAnalyzer {
    private boolean matchesDateFormat(String date) {
        try {
            int year = Integer.parseInt(date.substring(0, date.indexOf('-')));
            int month = Integer.parseInt(date.substring(date.indexOf('-') + 1, date.lastIndexOf('-')));
            int day = Integer.parseInt(date.substring(date.lastIndexOf('-') + 1));
            LocalDate.of(year, month, day);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    public Map<String, Integer> countEntriesInZipFile(String searchString,
                                                      File file,
                                                      LocalDate startDate,
                                                      Integer numberOfDays) {
        Map<String, Integer> map = new HashMap<>();
        LocalDate toDate = startDate.plusDays(numberOfDays);
        try (ZipFile zip = new ZipFile(file)) {
            File[] tempFiles = writeToTemp(zip);
            //Iterate over files extracted from a ZIP file to a temporary directory.
            for (File tempFile : tempFiles) {
                String fileName = tempFile.getName();
                //Check if name of the file is matching a date pattern.
                if (matchesFileRegex(fileName)) {
                    LocalDate date = LocalDate.parse(fileName.substring(5, 15),
                            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
                    if (date.isAfter(startDate.minusDays(1)) && date.isBefore(toDate)) {
                        AtomicInteger i = new AtomicInteger();
                        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                            reader.lines().forEach(line -> {
                                if (line.contains(searchString)) {
                                    i.getAndIncrement();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //For each file matching date pattern count number of lines that contains search string.
                        map.put(fileName, i.get());
                    }
                }
            }
        } catch (IOException e) {
            return new HashMap<>();
        }
        //Return a Map with a file name as a key, and number of lines with search string as a value.
        return map;
    }

    public boolean matchesFileRegex(String fileName) {
        if (!fileName.matches(Constants.FILE_REGEX)) {
            return false;
        }
        return matchesDateFormat(fileName.substring(5, 15));
    }

    private File[] writeToTemp(ZipFile zip) {
        zip.stream().forEach(entry -> {
            Path outputFile = Constants.TEMP_PATH.resolve(entry.getName());
            try (InputStream inputStream = zip.getInputStream(entry);
                 FileOutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return Constants.TEMP_PATH.toFile().listFiles();
    }
}
