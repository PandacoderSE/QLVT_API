package DATN.ITDeviceManagement.DTO.request;

import lombok.Data;

import java.util.Date;
@Data
public class DeviceUpdateRequest {
    private Long id;
    private String accountingCode;
    private String manufacture;
    private String notes;
    private Date purchaseDate;
    private String serviceTag;
    private String purpose;
    private String serialNumber;
    private String specification;
    private Long categoryId;
    private Date expirationDate;
}
