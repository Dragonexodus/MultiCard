package helper;

public class SuccessResult<T> extends Result<T> {
    public SuccessResult(T data) {
        super();
        setSuccess(true);
        setData(data);
    }
}
