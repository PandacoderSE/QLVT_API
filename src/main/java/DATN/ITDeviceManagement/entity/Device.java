package DATN.ITDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountingCode;
    private String serialNumber;
    private String specification;
    private String manufacture;
    private String location;
    private Date purchaseDate;
    private String purpose;
    private Date expirationDate;
    private String notes;
    @Column(columnDefinition = "TEXT")
    private String identifyCode;
    private String serviceTag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    private String status;

    @OneToMany(mappedBy = "device", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DeviceAssignment> deviceAssignments = new ArrayList<>();

}
