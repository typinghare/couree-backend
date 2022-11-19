package couree.com.luckycat.glacier.exception;

public class ConversionFailException extends RuntimeException {
    public ConversionFailException(String name, Class<?> classOfValue) {
        super(String.format("Global variable [ %s ] should be: [ %s ]", name, classOfValue.getName()));
    }
}
