package helper;

/**
 * Created by Patrick on 23.06.2015.
 */
public class SuccessResult<T> extends Result<T> {
    public SuccessResult(T data) {
        super();
        this.setSuccess(true);
        this.setData(data);
    }
}
