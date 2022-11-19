package couree.com.luckycat.glacier.exception;

public class DuplicateApplicationNameException extends RuntimeException {
    public DuplicateApplicationNameException(String applicationName) {
        super(String.format("Duplicate application name: [ %s ].", applicationName));
    }
}
