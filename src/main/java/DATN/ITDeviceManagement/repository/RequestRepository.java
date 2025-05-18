package DATN.ITDeviceManagement.repository;

import DATN.ITDeviceManagement.constant.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DATN.ITDeviceManagement.entity.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    // Lấy danh sách phản hồi theo userId
    List<Request> findByUserId(String userId);

    // Tìm kiếm theo userId hoặc status (dành cho admin/manager, đã có trước đó)
    @Query("SELECT r FROM Request r WHERE (:userId IS NULL OR r.user.id = :userId) AND (:status IS NULL OR r.status = :status)")
    List<Request> findByUserIdOrStatus(@Param("userId") String userId, @Param("status") RequestStatus status);
    // Lấy danh sách phản hồi theo toUserId (người nhận)
    List<Request> findByToUserId(String toUserId);
}
