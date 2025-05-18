package DATN.ITDeviceManagement.exception;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseException extends ResponseStatusException {
    public CustomResponseException(HttpStatus status) {
        super(status);
    }

    public CustomResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    @Override
    public String getMessage() {
        String msg = (super.getReason() != null ? super.getReason()  : "");
        return NestedExceptionUtils.buildMessage(msg, getCause());
    }

}
