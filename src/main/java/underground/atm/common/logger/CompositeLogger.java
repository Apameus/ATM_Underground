package underground.atm.common.logger;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class CompositeLogger implements Logger{

    private final Logger[] loggers;
    private final String operatorName;

    public CompositeLogger(String operatorName, Logger... loggers) {
        this.operatorName = operatorName;
        this.loggers = loggers;
    }

    @Override
    public void log(String format, Object... args) {
        String date = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);

        String log = "[%s] %s / %s".formatted(operatorName, String.format(format, args), date);
        for (Logger logger : loggers) {
            logger.log(log);
        }
    }
}
