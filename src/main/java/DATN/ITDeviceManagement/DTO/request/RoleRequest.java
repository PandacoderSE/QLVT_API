package DATN.ITDeviceManagement.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    private String name;
    private String description;
    private List<String> permissions ;
}
