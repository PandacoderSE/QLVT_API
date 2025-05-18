package DATN.ITDeviceManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable{
    @Id
    private String id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private int status;
    @Column(name = "signature_path")
    private String signaturePath;

    @Column(name = "encryption_key")
    private String encryptionKey; // Lưu khóa AES (mã hóa bằng bcrypt)
    @OneToMany(mappedBy = "toUser", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DeviceAssignment> deviceAssignments = new ArrayList<>();
    @OneToMany(mappedBy = "createdBy", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    private List<Role> roles = new ArrayList<>();
}
