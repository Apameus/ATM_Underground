package underground.atm.common.logger;

public final class ConsoleLogger implements Logger{
    @Override
    public void log(String format, Object... args) {
        System.out.printf(format, args);
        System.out.printf("%n");
    }
}
