package DATN.ITDeviceManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import DATN.ITDeviceManagement.DTO.UserDTO;
import DATN.ITDeviceManagement.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String name) ;
    boolean existsByUsername(String username) ;
    @Query(value = "SELECT DISTINCT u.id, u.firstname, u.lastname, u.email, u.username " +
            "FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON ur.role_id = r.id " +
            "WHERE r.name IN ('ADMIN', 'MANAGER')",
            nativeQuery = true)
    List<Object[]> findAdminsAndManagersNative();

    default List<UserDTO> findAdminsAndManagers() {
        return findAdminsAndManagersNative().stream()
                .map(row -> new UserDTO((String) row[0], (String) row[1], (String) row[2], (String) row[3], (String) row[4]))
                .collect(Collectors.toList());
    }
}
