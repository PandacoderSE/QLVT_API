package DATN.ITDeviceManagement.entity;

import DATN.ITDeviceManagement.constant.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device_assignment")

public class DeviceAssignment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    private String handoverPerson ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    private int quantity;

    private LocalDateTime handoverDate;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status; // Thêm trường status
    @Column(name = "pdf_path")
    private String pdfPath;
}