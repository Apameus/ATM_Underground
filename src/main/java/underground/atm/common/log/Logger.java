package underground.atm.common.log;

public interface Logger {

    void log(String format, Object... args);


    interface Factory {
        Logger create(String operatorClassName);
    }

}
