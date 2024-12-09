package underground.atm.common.logger;

public interface Logger {

    void log(String format, Object... args);

    interface Factory {
        Logger create(String name);
    }

    final class CompositeLoggerFactory implements Factory{
        private final Logger[] loggers;

        public CompositeLoggerFactory(Logger... loggers) {
            this.loggers = loggers;
        }

        @Override
        public Logger create(String name) {
            return new CompositeLogger(name, loggers);
        }
    }
}
