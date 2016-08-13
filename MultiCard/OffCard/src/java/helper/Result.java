package helper;

public abstract class Result<T> {
    private T data;
    private boolean success;
    private String errorMsg;

    protected Result() {
    }

//    public Result(boolean success, T data) {
//        this.data = data;
//        this.success = success;
//    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    protected void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
