package DATN.ITDeviceManagement.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUseDevice {
    private String id;
    private String name ;
    private String email;
    private String phone;
    private int status;
    private List<String> listDevice ;
}
