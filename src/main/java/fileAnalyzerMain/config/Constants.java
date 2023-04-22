package fileAnalyzerMain.config;

import java.nio.file.Path;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String FILE_REGEX = "logs_\\d{4}-\\d{2}-\\d{2}-access\\.log";
    public static final Path ARCHIVE_PATH = Path.of("src/test/resources/logs-27_02_2018-03_03_2018.zip");
    public static final Path TEMP_PATH = Path.of("src/main/resources/temp");
}
