package DATN.ITDeviceManagement.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelResponse {
    private String accountingCode;
    private String serialNumber;
    private String specification;
    private String manufacture;
    private String location;
    private Date purchaseDate;
    private String purpose;
    private Date expirationDate;
    private String notes;
    private LocalDateTime updatedTime;
    private String categoryName; //
    private Long categoryId;
    private String ownerId;
    private String ownerName; //
    private Long departmentID;
    private String status;
    private String identifyCode = "";
}
