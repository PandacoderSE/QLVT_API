package DATN.ITDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class Notification extends  Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
