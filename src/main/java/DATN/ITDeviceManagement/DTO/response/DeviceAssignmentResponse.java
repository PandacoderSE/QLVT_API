package DATN.ITDeviceManagement.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import DATN.ITDeviceManagement.constant.AssignmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAssignmentResponse {
    private Long id;
    private Long deviceId;
    private String serialNumber;
    private String manufacturer;
    private String userId;
    private int quantity;
    private LocalDateTime handoverDate;
    private String handoverPerson ;
    private AssignmentStatus status;
    private String pdfPath;
}