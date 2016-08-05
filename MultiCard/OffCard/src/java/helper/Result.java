package helper;

/**
 * Created by Patrick on 23.06.2015.
 */
public abstract class Result<T> {
    private T data;
    private boolean success;
    private String errorMessage;

    protected Result() {
    }

    public Result(boolean success, T data) {
        this.data = data;
        this.success = success;
    }

    public T get() {
        return data;
    }

    protected void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
