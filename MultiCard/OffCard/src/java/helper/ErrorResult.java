package helper;

public class ErrorResult<T> extends Result<T> {
    public ErrorResult(String errorMessage, Object... args) {
        super();
        this.setSuccess(false);
        this.setErrorMessage(String.format(errorMessage, args));
    }
}
