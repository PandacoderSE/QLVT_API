package DATN.ITDeviceManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DATN.ITDeviceManagement.entity.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name) ;
    List<Role> findAllByName(String name) ;


}
