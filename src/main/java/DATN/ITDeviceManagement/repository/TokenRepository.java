package DATN.ITDeviceManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DATN.ITDeviceManagement.entity.InvalidatedToken;

public interface TokenRepository extends JpaRepository<InvalidatedToken, String> {
}
