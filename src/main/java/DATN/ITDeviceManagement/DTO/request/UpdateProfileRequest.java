package DATN.ITDeviceManagement.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest  {
    private String firstname;
    private String lastname;
    @Email(message = "Email không hợp lệ")
    private String email;
    private String phone;
}

