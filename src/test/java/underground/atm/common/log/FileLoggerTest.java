package underground.atm.common.log;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileLoggerTest {

    @Test
    @DisplayName("Should create new file and append at the end")
    void shouldCreateNewFileAndAppendAtTheEnd(@TempDir Path path) throws IOException {
//        Path tempFile = Files.createTempFile("temp-", "");
        Path logFile = path.resolve("log.txt");
        FileLogger fileLogger = new FileLogger(logFile);

        assertThat(logFile).exists();

        fileLogger.log("%s %s!", "Hello","Gentlemen");

        assertThat(logFile).hasContent("Hello Gentlemen!" + System.lineSeparator());

        fileLogger.log("Today is your lucky day.");

        assertThat(logFile).hasContent("""
                Hello Gentlemen!
                Today is your lucky day.
                """);
    }

    @Test
    @DisplayName("Should append at the end of the file")
    void shouldAppendAtTheEndOfTheFile(@TempDir Path path) throws IOException {
        Path logFile = path.resolve("log.txt");

        Files.writeString(logFile, """
                This is log 1
                This is log 2
                """);

        FileLogger fileLogger = new FileLogger(logFile);
        fileLogger.log("This is log 3");

        assertThat(logFile).hasContent("""
                This is log 1
                This is log 2
                This is log 3
                """);
    }

}