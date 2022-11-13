package couree.com.luckycat.dto;

import couree.com.luckycat.constant.JSendStatus;
import couree.com.luckycat.core.base.Dto;

/**
 * @author James Chan
 * @see <a href="https://github.com/omniti-labs/jsend">JSend</a>
 */
public class JSendDto extends Dto {
    private JSendStatus status;

    private String message;

    /**
     * Request Error code.
     */
    private String errorCode;

    private Object data;

    public JSendDto() {
    }

    public JSendStatus getStatus() {
        return status;
    }

    public void setStatus(JSendStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
