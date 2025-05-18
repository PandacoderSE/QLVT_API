package DATN.ITDeviceManagement.DTO.request;

import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@Builder
public class DeviceListRequest {
    private int page;
    private int pageSize;
    private String accountingCode;
    private String location;
    private String manufacture;
    private String notes;
    private String serviceTag;
    private Date purchaseDate;
    private String purpose;
    private String serialNumber;
    private Long categoryId;
    private Date expirationDate;
    private Long ownerId;
}
