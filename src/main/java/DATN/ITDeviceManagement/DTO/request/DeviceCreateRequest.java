package DATN.ITDeviceManagement.DTO.request;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceCreateRequest {
    private String accountingCode;
    private String location;
    private String manufacture;
    private String notes;
    private Date purchaseDate;
    private String purpose;
    private String serviceTag;
    private String serialNumber;
    private String specification;
    private Long categoryId;
    private Date expirationDate;
    private String identifyCode;
}
