package DATN.ITDeviceManagement.repository;

import DATN.ITDeviceManagement.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import DATN.ITDeviceManagement.DTO.request.DeviceListRequest;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query(value = "CALL list_device('LIST', :#{#params.page}, :#{#params.pageSize}, :#{#params.accountingCode}, " +
            ":#{#params.location}, :#{#params.manufacture}, :#{#params.notes}, " +
            ":#{#params.purchaseDate}, :#{#params.purpose}, :#{#params.serialNumber}," +
            ":#{#params.categoryId}, :#{#params.expirationDate})", nativeQuery = true)
    List<Device> getDevices(DeviceListRequest params);

    Device findBySerialNumber(String serialNumber);

    Device findByAccountingCode(String accountingCode);

    @Query(value = "CALL list_device('COUNT', :#{#params.page}, :#{#params.pageSize}, :#{#params.accountingCode}, " +
            ":#{#params.location}, :#{#params.manufacture}, :#{#params.notes}, " +
            ":#{#params.purchaseDate}, :#{#params.purpose}, :#{#params.serialNumber}," +
            ":#{#params.categoryId}, :#{#params.expirationDate})", nativeQuery = true)
    Long countTotalDevices(DeviceListRequest params);

    @Query("SELECT c.id, c.name, COUNT(d) " + "FROM Device d " + "INNER JOIN d.category c " + "GROUP BY c.id, c.name")
    List<Object[]> findDeviceQuantityByCategory();

    @Query("SELECT d.serialNumber,d.manufacture, d.category.name, d.specification, d.purchaseDate, da.toUser.id, d.status " +
            "FROM Device d " +
            "LEFT JOIN DeviceAssignment da ON d.id = da.device.id " +
            "AND da.id = (SELECT MAX(da2.id) FROM DeviceAssignment da2 WHERE da2.device.id = d.id AND da2.status IN ('ASSIGNED', 'PENDING')) " +
            "WHERE d.status = :status")
    List<Object[]> findByStatus(@Param("status") String status);


//    @Query("SELECT d.serialNumber, d.category.name, d.specification, d.purchaseDate, d.owner_id, d.status  FROM Device d WHERE (:serialNumber IS NULL OR d.serialNumber = :serialNumber) "
//            + "AND (:fromDate IS NULL OR d.purchaseDate >= :fromDate) " + "AND (:toDate IS NULL OR d.purchaseDate <= :toDate) "
//            + "AND (:categoryId IS NULL OR d.category.id = :categoryId) "
//            + "AND (:ownerId IS NULL OR d.owner_id = :ownerId) "
//            + "AND (:status IS NULL OR d.status = :status)"
//    )
//    List<Object[]> findByCriteria(@Param("serialNumber") String serialNumber,
//                                  @Param("fromDate") Date fromDate,
//                                  @Param("toDate") Date toDate,
//                                  @Param("categoryId") Long categoryId,
//                                  @Param("ownerId") String ownerId,
//                                  @Param("status") String status
//    );
        @Query("SELECT d.serialNumber,d.manufacture, d.category.name, d.specification, d.purchaseDate, da.toUser.id, d.status " +
                "FROM Device d " +
                "LEFT JOIN DeviceAssignment da ON d.id = da.device.id " +
                "AND da.id = (SELECT MAX(da2.id) FROM DeviceAssignment da2 WHERE da2.device.id = d.id AND da2.status = 'ASSIGNED') " +
                "WHERE (:serialNumber IS NULL OR d.serialNumber = :serialNumber) " +
                "AND (:fromDate IS NULL OR d.purchaseDate >= :fromDate) " +
                "AND (:toDate IS NULL OR d.purchaseDate <= :toDate) " +
                "AND (:categoryId IS NULL OR d.category.id = :categoryId) " +
                "AND (:ownerId IS NULL OR da.toUser.id = :ownerId) " +
                "AND (:status IS NULL OR d.status = :status)")
        List<Object[]> findByCriteria(
                @Param("serialNumber") String serialNumber,
                @Param("fromDate") Date fromDate,
                @Param("toDate") Date toDate,
                @Param("categoryId") Long categoryId,
                @Param("ownerId") String ownerId, // Sửa thành Long vì userId là Long
                @Param("status") String status
        );
    @Modifying
    @Transactional
    @Query("UPDATE Device d SET d.category = null WHERE d.category.id = :categoryId")
    void updateCategoryIdToNull(@Param("categoryId") Long categoryId);

    @Query("SELECT d.serialNumber,d.manufacture, d.category.name, d.specification, d.purchaseDate, d.status, d.accountingCode, d.identifyCode FROM Device d  WHERE d.serialNumber = :serialNumber")
    List<Object[]> getDeviceBySerialNum(String serialNumber);


    @Query("SELECT d.serialNumber, d.status FROM Device d WHERE d.status IN ('CHUA_SU_DUNG', 'DA_SU_DUNG')")
    List<Object[]> getDeviceQuantityByStatus();

}
