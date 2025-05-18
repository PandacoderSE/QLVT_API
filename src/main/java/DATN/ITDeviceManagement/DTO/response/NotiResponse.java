package DATN.ITDeviceManagement.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiResponse {
    private Long id ;
    private String title;
    private String content ;
    private String createby ;
    private LocalDateTime createdTime ;
}
