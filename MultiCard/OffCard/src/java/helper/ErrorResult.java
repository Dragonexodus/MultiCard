package helper;

public class ErrorResult<T> extends Result<T> {
    public ErrorResult(String errorMessage, Object... args) {
        super();
        this.setSuccess(false);
        this.setErrorMsg(String.format(errorMessage, args));
    }
}
