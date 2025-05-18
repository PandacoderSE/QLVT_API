package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.DTO.request.DeviceCreateRequest;
import DATN.ITDeviceManagement.DTO.request.DeviceUpdateRequest;
import DATN.ITDeviceManagement.DTO.response.PageResponse;
import org.springframework.core.io.FileSystemResource;
import DATN.ITDeviceManagement.DTO.request.DeviceListRequest;
import DATN.ITDeviceManagement.DTO.request.DeviceUserRequest;
import DATN.ITDeviceManagement.DTO.response.DeviceAssignmentResponse;
import DATN.ITDeviceManagement.DTO.response.DeviceResponse;
import DATN.ITDeviceManagement.constant.AssignmentStatus;
import DATN.ITDeviceManagement.entity.Device;

import java.util.Date;
import java.util.List;

public interface IDeviceService {
    DeviceResponse getDeviceResponseById(Long id);

    PageResponse<DeviceResponse> getDevices(DeviceListRequest request);

    DeviceResponse createDevice(DeviceCreateRequest request);

    DeviceResponse updateDevice(DeviceUpdateRequest request);

    void deleteDevice(List<Long> ids);

    public List<DeviceResponse> getAllDevices();

    public List<Object[]> countDeviceByCategory();


    Device findDeviceById(Long id);

    public List<Object[]> findDeviceByStatus(String status);

    public DeviceAssignmentResponse  setDeviceForOwner(DeviceUserRequest deviceUserRequest);

    public List<Object[]> searchDeviceByCustomField(String serialNumber, Date fromDate, Date toDate, Long categoryId, String ownerId, String status);

    public List<Object[]> getDeviceBySerialNum(String serialNumber);

    public List<Object[]> getDeviceQuantityByStatus();

    public DeviceResponse transferUsedDevice(DeviceUserRequest deviceUserRequest);

    public DeviceResponse transferEmptyStatusDevice(String serialNumber);
    List<DeviceResponse> approveDeviceAssignment(List<Long> deviceIds);
    List<DeviceAssignmentResponse> getAssignmentsByUserId(AssignmentStatus status, String serialNumber) ;
    // từ chối
    DeviceAssignmentResponse rejectDeviceAssignment(Long assignmentId) ;
    // trả lại.
    DeviceAssignmentResponse returnDeviceAssignment(Long assignmentId) ;

    FileSystemResource downloadHandoverPdf(Long assignmentId) ;
}
