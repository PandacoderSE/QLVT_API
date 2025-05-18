package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.constant.RequestStatus;
import DATN.ITDeviceManagement.DTO.request.RequestDTO;
import DATN.ITDeviceManagement.DTO.response.RequestResponse;

import java.util.List;

public interface IRequestService {
    // Lấy danh sách phản hồi của Staff
    List<RequestResponse> getRequestsByUserId(String userId);

    // Gửi phản hồi mới
    RequestResponse createRequest(RequestDTO createRequestDTO);

    // Lấy chi tiết phản hồi
    RequestResponse getRequestById(Long requestId, String userId);

    // Các phương thức dành cho admin/manager
    List<RequestResponse> getAllRequests();

    RequestResponse respondToRequest(Long requestId, String responseContent);

    RequestResponse approveRequest(Long requestId);

    void deleteRequest(Long requestId);

    List<RequestResponse> searchRequests(String userId, RequestStatus status);
}
