package fileAnalyzerMain;

import fileAnalyzerMain.config.Constants;
import fileAnalyzerMain.services.LogsAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;

@SpringBootTest
class FileAnalyzerApplicationTests {
    private final LogsAnalyzer analyzer = new LogsAnalyzer();
    private Map<String, Integer> result;

    @Test
    public void testLogNames() {
        Assertions.assertTrue(analyzer.matchesFileRegex("logs_2018-08-31-access.log"));
        Assertions.assertTrue(analyzer.matchesFileRegex("logs_2018-01-31-access.log"));
        Assertions.assertTrue(analyzer.matchesFileRegex("logs_2020-02-29-access.log"));
        Assertions.assertFalse(analyzer.matchesFileRegex("log_2018-13-31-access.log"));
        Assertions.assertFalse(analyzer.matchesFileRegex("logs_2018-02-31-access.log"));
        Assertions.assertFalse(analyzer.matchesFileRegex("logs_2018-09-31.log"));
        Assertions.assertFalse(analyzer.matchesFileRegex("logs_22-12-2020-access.log"));
    }

    @Test
    public void testCount() {
        result = analyzer.countEntriesInZipFile("Mozilla",
                Constants.ARCHIVE_PATH.toFile(),
                LocalDate.of(2018, 2, 27),
                3);
        Map<String, Integer> expected = Map.of("logs_2018-02-27-access.log",
                40,
                "logs_2018-02-28-access.log",
                18,
                "logs_2018-03-01-access.log",
                23);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testIncorrectSearchString() {
        result = analyzer.countEntriesInZipFile("Что-то на русском",
                Constants.ARCHIVE_PATH.toFile(),
                LocalDate.of(2018, 2, 27),
                3);
        Map<String, Integer> expected = Map.of("logs_2018-02-27-access.log",
                0,
                "logs_2018-02-28-access.log",
                0,
                "logs_2018-03-01-access.log",
                0);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testNoSuchFiles() {
        result = analyzer.countEntriesInZipFile("Mozilla",
                Constants.ARCHIVE_PATH.toFile(),
                LocalDate.of(2023, 11, 11),
                11);
        Assertions.assertTrue(result.isEmpty());
    }
}
