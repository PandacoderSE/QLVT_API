package DATN.ITDeviceManagement.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestResponse {
    private Long id;
    private String content;
    private LocalDateTime requestDate;
    private String status;
    private String title;
    private String userId; // Người gửi
    private String toUserId; // Người nhận
    private String approvedBy;
    private LocalDateTime approvedDate;
}
