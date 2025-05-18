package DATN.ITDeviceManagement.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;


public enum ErrorCode {
    EXISTING_ACCOUNTING_CODE_DEVICE("409", "Accounting code đã tồn tại"),
    EXISTING_SERIAL_NUMBER_DEVICE("409", "Serial number đã tồn tại"),
    NON_EXISTING_ID_DEVICE("404", "Không có thiết bị tồn tại với id này"),
    NON_EXISTING_ID_CATEGORY("404", "Không có danh mục tồn tại với id này"),
    NON_EXISTING_ID_USER("404" , "Không có người dùng tồn tại với id này"),
    NON_EXISTING_ID_NOTIFICATION("404" , "Không có thông báo này"),
    NON_EXISTING_ROLE("404" , "Không tồn tại role này"),

    NON_EXISTING_ID_OWNER("404" , "Không có chủ sở hữu tồn tại với id này"),
    INTERNAL_SERVER_ERROR("500", "Lỗi nội bộ máy chủ"),
    UNAUTHENTICATED("401", "Xác thực thất bại"),

    USED_DEVICE("409", "Thiết bi này đã được sử dụng"),
    INVALID_DATE_COMPARISON(HttpStatus.BAD_REQUEST.toString(), "ngày hết hạn phải lớn hơn ngày mua"),
    EXISTING_USER("409", "Người dùng đã tồn tại"),
    NOT_FOUND_CATEGORY("404", "Danh mục chưa tồn tại"),
    EXISTING_CATEGORY("409", "Danh mục này đã tồn tại"),
    NONSTATUS("403", "Tài khoản của bạn hiện không hoạt động. Vui lòng liên hệ hỗ trợ để được hỗ trợ.");

    private final String code;
    private final String message;


    ErrorCode(String code, String message) { this.code = code; this.message = message;}

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
