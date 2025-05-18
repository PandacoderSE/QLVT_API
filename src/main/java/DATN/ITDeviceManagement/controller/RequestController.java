package DATN.ITDeviceManagement.controller;

import DATN.ITDeviceManagement.DTO.request.RequestDTO;
import DATN.ITDeviceManagement.constant.RequestStatus;
import DATN.ITDeviceManagement.service.IRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import DATN.ITDeviceManagement.DTO.response.RequestResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    @Autowired
    private IRequestService requestService;

    // API dành cho Staff: Lấy danh sách phản hồi của mình
    @GetMapping("/my-requests")
    public ResponseEntity<List<RequestResponse>> getMyRequests(
            @RequestParam String userId) {
        return ResponseEntity.ok(requestService.getRequestsByUserId(userId));
    }

    // API dành cho Staff: Gửi phản hồi mới
    @PostMapping("/create")
    public ResponseEntity<RequestResponse> createRequest(
            @Valid @RequestBody RequestDTO createRequestDTO) {
        return ResponseEntity.ok(requestService.createRequest(createRequestDTO));
    }

    // API dành cho Staff: Xem chi tiết phản hồi

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestResponse> getRequestById(
            @PathVariable Long requestId,
            @RequestParam String userId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId, userId));
    }

    // API dành cho admin/manager: Lấy danh sách các request được gửi đến họ

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @PostMapping("/{requestId}/respond")
    public ResponseEntity<RequestResponse> respondToRequest(
            @PathVariable Long requestId,
            @RequestParam String responseContent) {
        return ResponseEntity.ok(requestService.respondToRequest(requestId, responseContent));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<RequestResponse> approveRequest(
            @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.approveRequest(requestId));
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RequestResponse>> searchRequests(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) RequestStatus status) {
        return ResponseEntity.ok(requestService.searchRequests(userId, status));
    }
}
