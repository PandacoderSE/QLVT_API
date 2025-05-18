package DATN.ITDeviceManagement.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUserRequest {
    private String serial_number;
    private String owner_id;
}
