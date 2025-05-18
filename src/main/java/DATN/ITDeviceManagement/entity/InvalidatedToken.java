package DATN.ITDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "InvalidatedToken")
public class InvalidatedToken extends Auditable{
    @Id
    private String ID ;
    private Date exoiryTime ;
}
